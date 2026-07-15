package com.NMCNPM.ABT_bio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String id;
    private Instant date;
    private String customerName;
    private String email;
    private String phone;
    private String address;
    private String organization;
    private String paymentMethod;
    private List<OrderItemResponse> items;
    private String status;
    private BigDecimal total;
    private String paymentStatus;
    private String notes;
}
