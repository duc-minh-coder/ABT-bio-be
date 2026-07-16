package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.entity.PaymentTransactions;
import com.NMCNPM.ABT_bio.entity.PaymentWebhook;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.enums.*;
import com.NMCNPM.ABT_bio.exception.AppException;
import com.NMCNPM.ABT_bio.exception.ErrorCode;
import com.NMCNPM.ABT_bio.repository.OrderRepository;
import com.NMCNPM.ABT_bio.repository.PaymentTransactionRepository;
import com.NMCNPM.ABT_bio.repository.PaymentWebhookRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.OrderService;
import com.NMCNPM.ABT_bio.service.PaymentProviderDispatcherService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentTransactionRepository paymentTransactionRepository;
    UserRepository userRepository;
    PaymentWebhookRepository paymentWebhookRepository;
    OrderRepository orderRepository;

//    LedgerService ledgerService;
    OrderService orderService;
    PaymentProviderDispatcherService paymentProviderDispatcherService;
    PaymentService self; // Inject chính nó để dùng Transaction nội bộ

    public PaymentService(
            PaymentTransactionRepository paymentTransactionRepository,
            UserRepository userRepository,
            PaymentWebhookRepository paymentWebhookRepository,
            OrderRepository orderRepository,
//            LedgerService ledgerService,
            OrderService orderService,
            @Lazy PaymentProviderDispatcherService paymentProviderDispatcherService,
            @Lazy PaymentService self
    ) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.userRepository = userRepository;
        this.paymentWebhookRepository = paymentWebhookRepository;
        this.orderRepository = orderRepository;
//        this.ledgerService = ledgerService;
        this.orderService = orderService;
        this.paymentProviderDispatcherService = paymentProviderDispatcherService;
        this.self = self;}

    // ========================================================================
    // 1. DEPOSIT FLOW (NẠP TIỀN)
    // ========================================================================

    @Transactional
    public PaymentTransactions createPendingTransaction(
            UUID userId,
            UUID systemOrderId,
            String providerCode,
            BigDecimal amount,
            String currency,
            PaymentMethodEnum method,
            PaymentProviderEnum provider,
            PaymentTypeEnum type
    ) {

//        if (paymentTransactionRepository.findByOrderId(systemOrderId).isPresent()) {
//            throw new IllegalStateException("Order " + systemOrderId + " already has a transaction");
//        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Orders systemOrder = null;
        if (systemOrderId != null) {
            systemOrder = orderRepository.findById(systemOrderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if (systemOrder.getStatus() == OrderStatusEnum.PAID ||
                    systemOrder.getStatus() == OrderStatusEnum.COMPLETED) {
                throw new AppException(ErrorCode.ORDER_ALREADY_PAID);
            }
        }

        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String txCode = "TX" + System.currentTimeMillis() + randomSuffix;

        PaymentTransactions tx = PaymentTransactions.builder()
                .user(user)
                .order(systemOrder)
                .transactionCode(txCode)
                .providerEventId(providerCode)
                .amount(amount)
                .currency(currency)
                .paymentMethod(method)
                .provider(provider)
                .type(type)
                .status(PaymentStatusEnum.PENDING)
                .build();

        tx = paymentTransactionRepository.save(tx);

        if (systemOrder != null) {
            systemOrder.setPaymentTransaction(tx);
            orderRepository.save(systemOrder);
        }

        return tx;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finalizePaymentAndLedger(PaymentTransactions tx, PaymentStatusEnum status,
                                         PaymentProviderEnum provider, String providerEventId,
                                         String rawPayload) {

        if (tx.getStatus() == PaymentStatusEnum.SUCCESS) {
            return;
        }

        tx.setStatus(status);
        tx.setProvider(provider);
        tx.setProviderEventId(providerEventId);
        tx.setProviderStatus(status.name());
        tx.setWebhookLog(appendLog(tx.getWebhookLog(), rawPayload));

        paymentTransactionRepository.save(tx);

        // Chỉ xử lý nếu thanh toán thành công và là thanh toán đơn hàng
        if (status == PaymentStatusEnum.SUCCESS && tx.getType() == PaymentTypeEnum.ORDER_PAYMENT) {
            // Đánh dấu đơn hàng là ĐÃ THANH TOÁN (PAID)
            orderService.markOrderAsPaid(tx);
        }
    }

    // ========================================================================
    // 2. WEBHOOK PROCESSING
    // ========================================================================

    @Transactional
    public void processWebhook(PaymentProviderEnum provider, String providerEventId,
                               String content, BigDecimal amount, String providerStatus,
                               String rawPayload) {

        // Idempotency Check
        if (paymentWebhookRepository.existsByProviderEventId(providerEventId)) {
            log.info("Webhook event {} already processed", providerEventId);
            return;
        }

        PaymentWebhook wh = PaymentWebhook.builder()
                .provider(provider.name())
                .providerEventId(providerEventId)
                .payload(rawPayload)
                .processed(false)
                .build();
        try {
            // Ép lưu xuống DB ngay lập tức
            wh = paymentWebhookRepository.saveAndFlush(wh);
        } catch (DataIntegrityViolationException e) {
            log.warn("Đụng độ Webhook! Event {} đã được luồng khác xử lý.", providerEventId);
            return; // Dừng luôn luồng trùng lặp
        }

        try {
            String transactionCode = extractTxCode(content);

            PaymentTransactions tx = paymentTransactionRepository.findByTransactionCode(transactionCode)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionCode));

            if (tx.getAmount().compareTo(amount) != 0) {
                log.error("Amount mismatch! Tx: {}, Webhook: {}", tx.getAmount(), amount);
                throw new IllegalStateException("Amount mismatch");
            }

            PaymentStatusEnum mappedStatus = mapProviderStatus(providerStatus);

            // Gọi logic chính
            self.finalizePaymentAndLedger(tx, mappedStatus, provider, providerEventId, rawPayload);

            wh.setPayment(tx);
            wh.setProcessed(true);
            wh.setPaymentTxId(tx.getId());
            paymentWebhookRepository.save(wh);

        } catch (Exception e) {
            log.error("Error processing webhook {}: {}", providerEventId, e.getMessage());
            // Không throw exception để trả 200 OK cho Provider
        }
    }

    /**
     * Lấy trạng thái giao dịch để Frontend Polling
     */
    public String getTransactionStatus(UUID transactionId) {
        PaymentTransactions tx = paymentTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        // XỬ LÝ CHO ĐƠN HÀNG: Trả về trạng thái của Order (PAID, COMPLETED, PENDING, CANCELLED)
        if (tx.getType() == PaymentTypeEnum.ORDER_PAYMENT && tx.getOrder() != null) {
            return tx.getOrder().getStatus().name();
        }

        // XỬ LÝ CHO NẠP TIỀN/RÚT TIỀN: Trả về trạng thái của Transaction (SUCCESS, FAILED, PENDING)
        return tx.getStatus().name();
    }

    // ========================================================================
    // 4. HELPER METHODS
    // =======================================================================

    private String appendLog(String currentLog, String newPayload) {
        return (currentLog == null || currentLog.isEmpty()) ? newPayload : currentLog + "\n----------------\n" + newPayload;
    }

    private String extractTxCode(String content) {
        if (content == null) return "";
        Pattern pattern = Pattern.compile("\\b(TX|WD)-?[a-zA-Z0-9\\-]+\\b");
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group() : content;
    }

    private PaymentStatusEnum mapProviderStatus(String providerStatus) {
        if (providerStatus == null) return PaymentStatusEnum.FAILED;
        String s = providerStatus.toUpperCase();
        return (s.equals("SUCCESS") || s.equals("COMPLETED") || s.equals("PAID") || s.equals("00"))
                ? PaymentStatusEnum.SUCCESS : PaymentStatusEnum.FAILED;
    }

}