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
    private String customerName;

    private String email;

    private String phone;

    private String address;

    private String organization;

    private String paymentMethod;

    private List<CheckoutItemRequest> items;

    private BigDecimal total;

    private String notes;

    private String status;

    private String paymentStatus;
}
