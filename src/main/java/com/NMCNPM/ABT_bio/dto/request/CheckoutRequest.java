package com.NMCNPM.ABT_bio.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    @NotBlank(message = "CUSTOMER_NAME_REQUIRED")
    private String customerName;

    @NotBlank(message = "CUSTOMER_EMAIL_REQUIRED")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "PHONE_REQUIRED")
    private String phone;

    @NotBlank(message = "ADDRESS_REQUIRED")
    private String address;

    private String organization;

    @NotBlank(message = "PAYMENT_METHOD_REQUIRED")
    private String paymentMethod;

    @NotNull(message = "ITEMS_REQUIRED")
    private List<CheckoutItemRequest> items;

    @NotNull(message = "TOTAL_REQUIRED")
    private BigDecimal total;

    private String notes;

    private String status;

    private String paymentStatus;
}
