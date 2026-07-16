package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.dto.ApiResponse;
import com.NMCNPM.ABT_bio.dto.request.CreatePaymentRequest;
import com.NMCNPM.ABT_bio.dto.response.CreatePaymentResponse;
import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.entity.PaymentTransactions;
import com.NMCNPM.ABT_bio.enums.PaymentMethodEnum;
import com.NMCNPM.ABT_bio.enums.PaymentProviderEnum;
import com.NMCNPM.ABT_bio.enums.PaymentTypeEnum;
import com.NMCNPM.ABT_bio.exception.AppException;
import com.NMCNPM.ABT_bio.exception.ErrorCode;
import com.NMCNPM.ABT_bio.repository.OrderRepository;
import com.NMCNPM.ABT_bio.repository.PaymentTransactionRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.PaymentProviderServiceInterface;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.payos.PayOS;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PosService implements PaymentProviderServiceInterface {
    @Lazy
    PaymentService paymentService;
    OrderRepository orderRepository;

    UserRepository userRepository;
    ObjectMapper objectMapper;

    PaymentTransactionRepository paymentTransactionRepository;
    RestTemplate restTemplate = new RestTemplate(); // Dùng để gọi API Payout

    @NonFinal
    @Value("${payos.client-id}")
    String clientId;

    @NonFinal
    @Value("${payos.api-key}")
    String apiKey;

    @NonFinal
    @Value("${payos.checksum-key}")
    String checksumKey;

    @NonFinal
    @Value("${app.frontend.url}")
    String frontendUrl;

    @NonFinal
    PayOS payOS;
    @NonFinal
    PayOS payOSPayout;

    @PostConstruct
    public void init() {
        // Khởi tạo SDK PayOS
        this.payOS = new PayOS(clientId, apiKey, checksumKey);
    }

    @Override
    public PaymentProviderEnum provider() {
        return PaymentProviderEnum.PAYOS;
    }

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest req) {
        UUID userId = com.NMCNPM.ABT_bio.utils.SecurityUtils.getCurrentUserId();

        if (req.getOrderId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Orders order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getBuyer().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        req.setAmount(order.getTotalAmount());
        req.setCurrency(order.getCurrency());

        long payosOrderCode = System.nanoTime();

        PaymentTransactions tx = paymentService.createPendingTransaction(
                userId,
                req.getOrderId(),
                String.valueOf(payosOrderCode),
                req.getAmount(),
                req.getCurrency(),
                PaymentMethodEnum.BANK_TRANSFER,
                PaymentProviderEnum.PAYOS,
                PaymentTypeEnum.ORDER_PAYMENT
        );

        tx.setProviderEventId(String.valueOf(payosOrderCode));
        paymentTransactionRepository.save(tx);

        String shortDesc = order.getOrderCode().replace("-", "");
        if (shortDesc.length() > 25) {
            shortDesc = shortDesc.substring(0, 25);
        }

        try {
            ObjectNode body = buildCreatePaymentBody(payosOrderCode, req.getAmount().intValue(), shortDesc);
            HttpEntity<String> request = buildHttpRequest(body);

            JsonNode response = restTemplate.postForObject(
                    "https://api-merchant.payos.vn/v2/payment-requests",
                    request,
                    JsonNode.class
            );

            if (response == null || !"00".equals(response.path("code").asText())) {
                log.error("PayOS create payment failed: {}", response);
                throw new AppException(ErrorCode.PAYMENT_PROVIDER_ERROR);
            }

            JsonNode data = response.path("data");

            return CreatePaymentResponse.builder()
                    .transactionId(tx.getId())
                    .transactionCode(tx.getTransactionCode())
                    .amount(tx.getAmount())
                    .currency("VND")
                    .checkoutUrl(data.path("checkoutUrl").asText())
                    .qrCode(data.path("qrCode").asText())
                    .provider(PaymentProviderEnum.PAYOS.name())
                    .providerPayload(String.valueOf(payosOrderCode))
                    .build();

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Create PayOS payment error", e);
            throw new AppException(ErrorCode.PAYMENT_PROVIDER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> handleWebhook(String payload, Map<String, String> headers) {
        try {
            // Verify Webhook (Quan trọng để tránh giả mạo)
            Webhook webhookBody = objectMapper.readValue(payload, Webhook.class);
            WebhookData data = payOS.webhooks().verify(webhookBody);

            // Chuyển đổi an toàn sang String để khớp với DB
            String payosOrderCode = String.valueOf(data.getOrderCode());

            // Tìm Transaction trong DB bằng providerEventId (nơi ta đã lưu payosOrderCode)
            PaymentTransactions tx = paymentTransactionRepository.findByProviderEventId(payosOrderCode)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found for PayOS OrderCode: " + payosOrderCode));

            // TRICK: PaymentService của bạn đang extract transactionCode từ nội dung string.
            // Nhưng nội dung từ bank trả về qua PayOS có thể không chứa transactionCode của mình (do giới hạn ký tự).
            // Ta sẽ tự build một chuỗi content "giả" chứa transactionCode để PaymentService parse được.
            String fakeContentForLogic = "Payment success for " + tx.getTransactionCode();

            // 5. Gọi PaymentService xử lý logic chung (cộng tiền, log, v.v.)
            paymentService.processWebhook(
                    PaymentProviderEnum.PAYOS,
                    data.getReference(), // Mã giao dịch phía ngân hàng
                    fakeContentForLogic, // Content đã được inject TX code
                    new BigDecimal(data.getAmount()),
                    "SUCCESS", // PayOS webhook chỉ bắn khi thành công
                    payload
            );

            return ApiResponse.<String>builder()
                    .code(200)
                    .message("OK")
                    .build();
        } catch (Exception e) {
            log.error("Webhook Error (Ignored to keep PayOS happy): ", e);
//            throw new AppException(ErrorCode.PAYMENT_PROVIDER_ERROR);
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Error but received")
                    .build();
        }
    }

    // Hàm tạo chữ ký chung (HMAC-SHA256)
    private String createSignature(String dataStr) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(dataStr.getBytes(StandardCharsets.UTF_8)));
    }

    // Hàm ký riêng cho Payout (giữ nguyên logic cũ vì nó sắp xếp field khác)
    private String createSignatureOfPaymentRequest(int amount, String accountNumber, String accountName, String bank, String description) {
        try {
            String dataStr = "accountName=" + accountName +
                    "&accountNumber=" + accountNumber +
                    "&amount=" + amount +
                    "&bank=" + bank +
                    "&description=" + description;
            return createSignature(dataStr);
        } catch (Exception e) {
            log.error("Error creating signature", e);
            throw new RuntimeException("Cannot create signature");
        }
    }

    private ObjectNode buildCreatePaymentBody(long orderCode, int amount, String desc) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("orderCode", orderCode);
        body.put("amount", amount);
        body.put("description", desc);
        body.put("returnUrl", frontendUrl + "/payment/success");
        body.put("cancelUrl", frontendUrl + "/payment/cancel");

        String raw = "amount=" + amount +
                "&cancelUrl=" + frontendUrl + "/payment/cancel" +
                "&description=" + desc +
                "&orderCode=" + orderCode +
                "&returnUrl=" + frontendUrl + "/payment/success";

        body.put("signature", createSignature(raw));
        return body;
    }

    private HttpEntity<String> buildHttpRequest(ObjectNode body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);
        return new HttpEntity<>(body.toString(), headers);
    }

    /**
     * Hàm tạo chữ ký cho API Transfer của PayOS
     * Quy tắc: Sắp xếp các trường theo a-z và hash HMAC-SHA256
     */
//    private String createSignatureOfPaymentRequest(int amount, String accountNumber, String accountName, String bank, String description) {
//        try {
//            // Chuỗi cần ký format: accountName={}&accountNumber={}&amount={}&bank={}&description={}
//            // Lưu ý: Phải sắp xếp key theo alphabet
//            String dataStr = "accountName=" + accountName +
//                    "&accountNumber=" + accountNumber +
//                    "&amount=" + amount +
//                    "&bank=" + bank +
//                    "&description=" + description;
//
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secret_key = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//
//            // Yêu cầu thư viện Apache Commons Codec hoặc dùng Java native để convert byte sang Hex
//            return Hex.encodeHexString(sha256_HMAC.doFinal(dataStr.getBytes(StandardCharsets.UTF_8)));
//        } catch (Exception e) {
//            log.error("Error creating signature", e);
//            throw new RuntimeException("Cannot create signature");
//        }
//    }
}
