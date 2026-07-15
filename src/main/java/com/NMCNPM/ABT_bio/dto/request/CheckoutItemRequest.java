package com.NMCNPM.ABT_bio.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutItemRequest {
    @NotNull(message = "PRODUCT_ID_REQUIRED")
    private Long productId;

    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MIN_1")
    private Integer quantity;

    @NotNull(message = "PRICE_AT_ORDER_REQUIRED")
    private BigDecimal priceAtOrder;
}
