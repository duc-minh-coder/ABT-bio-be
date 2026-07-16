package com.NMCNPM.ABT_bio.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentResponse {
    UUID transactionId;       // internal UUID (payment_transactions.id)
    String transactionCode;   // internal code (TX-...)
    BigDecimal amount;
    String currency;          // "VND" or "USD"
    String provider;          // "SEPAY" | "PAYPAL"
    String checkoutUrl;       // qrUrl (Sepay) or approveUrl (PayPal)
    Instant expiresAt;        // optional: expiry for QR
    String providerPayload;   // optional raw provider response (if you want)

    String qrCode;
}
