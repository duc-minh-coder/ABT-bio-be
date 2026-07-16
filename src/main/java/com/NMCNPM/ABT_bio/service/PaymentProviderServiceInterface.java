package com.NMCNPM.ABT_bio.service;


import com.NMCNPM.ABT_bio.dto.ApiResponse;
import com.NMCNPM.ABT_bio.dto.request.CreatePaymentRequest;
import com.NMCNPM.ABT_bio.dto.response.CreatePaymentResponse;
import com.NMCNPM.ABT_bio.enums.PaymentProviderEnum;

import java.util.Map;

public interface PaymentProviderServiceInterface {
    PaymentProviderEnum provider();
    CreatePaymentResponse createPayment(CreatePaymentRequest req);
    ApiResponse<String> handleWebhook(String payload, Map<String,String> headers);
}
