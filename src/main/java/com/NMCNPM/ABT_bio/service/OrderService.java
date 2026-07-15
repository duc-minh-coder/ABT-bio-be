package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.dto.request.CheckoutRequest;
import com.NMCNPM.ABT_bio.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Orders checkout(String userEmail, CheckoutRequest req);
    Page<Orders> list(Pageable pageable);
}
