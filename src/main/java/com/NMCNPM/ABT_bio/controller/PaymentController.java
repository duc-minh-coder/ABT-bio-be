package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.ApiResponse;
import com.NMCNPM.ABT_bio.dto.request.CreatePaymentRequest;
import com.NMCNPM.ABT_bio.dto.response.CreatePaymentResponse;
import com.NMCNPM.ABT_bio.enums.PaymentProviderEnum;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.PaymentProviderDispatcherService;
import com.NMCNPM.ABT_bio.service.impl.PaymentService;
import com.NMCNPM.ABT_bio.service.PaymentProviderServiceInterface;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentProviderDispatcherService paymentProviderDispatcherService;
    PaymentService paymentService;

    UserRepository userRepository;

    /**
     * TẠO GIAO DỊCH POS (QR / máy quẹt / chuyển khoản)
     */
    @PostMapping("/payos/create")
    public ApiResponse<CreatePaymentResponse> createPosPayment(
            @RequestBody CreatePaymentRequest request
    ) {
        PaymentProviderServiceInterface service =
                paymentProviderDispatcherService.get(PaymentProviderEnum.PAYOS);

        return ApiResponse.<CreatePaymentResponse>builder()
                .result(service.createPayment(request))
                .code(200)
                .message("Tạo giao dịch PayOS thành công")
                .build();
    }

    /* API Polling để Frontend kiểm tra trạng thái thanh toán liên tục
     * URL: GET /api/payments/status/{transactionId}
     */
    @GetMapping("/status/{transactionId}")
    public ApiResponse<String> getPaymentStatus(@PathVariable UUID transactionId) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Lấy trạng thái thành công")
                .result(paymentService.getTransactionStatus(transactionId))
                .build();
    }
    /**
     * WEBHOOK POS (ngân hàng / cổng POS gọi vào)
     */
    @PostMapping("/webhook/payos")
    public ApiResponse<String> posWebhook(
            @RequestBody String payload,
            @RequestHeader Map<String, String> headers
    ) {
        System.out.println(payload);

        paymentProviderDispatcherService
                .get(PaymentProviderEnum.PAYOS)
                .handleWebhook(payload, headers);

        return ApiResponse.<String>builder()
                .code(200)
                .message("OK")
                .build();
    }
}
