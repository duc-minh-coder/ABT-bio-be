package com.NMCNPM.ABT_bio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal priceAtOrder;
}
