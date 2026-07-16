package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.dto.request.CheckoutRequest;
import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.entity.PaymentTransactions;
import com.NMCNPM.ABT_bio.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Orders checkout(UUID userId, CheckoutRequest req);
    Page<Orders> list(Pageable pageable);
    void markOrderAsPaid(PaymentTransactions tx);

    Page<Orders> listOrdersByStatus(UUID userId, boolean isAdmin, List<OrderStatusEnum> statuses, Pageable pageable);
}
