package com.NMCNPM.ABT_bio.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {
//    UUID userId;
    UUID orderId;
    BigDecimal amount;
    String currency;
    String provider;
    String paymentMethod; // VIETQR or PAYPAL
}
