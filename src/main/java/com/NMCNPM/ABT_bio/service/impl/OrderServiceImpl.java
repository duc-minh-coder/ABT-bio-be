package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.dto.request.CheckoutRequest;
import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.enums.OrderStatusEnum;
import com.NMCNPM.ABT_bio.repository.CartRepository;
import com.NMCNPM.ABT_bio.repository.OrderRepository;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Orders checkout(String userEmail, CheckoutRequest req) {
        Users user = userRepository.findByContactEmail(userEmail).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseThrow();

        Orders order = Orders.builder()
                .orderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .buyer(user)
                .buyerContent(toBuyerContent(req))
                .productName(req.getItems() != null && !req.getItems().isEmpty() ? req.getItems().get(0).getProductId().toString() : "")
                .quantity(req.getItems() != null ? req.getItems().stream().mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity()).sum() : 0)
                .totalAmount(req.getTotal() == null ? BigDecimal.ZERO : req.getTotal())
                .currency("VND")
                .status(mapStatus(req.getStatus()))
                .createdAt(Instant.now())
                .build();

        if (req.getItems() != null) {
            req.getItems().forEach(item -> {
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                if (product != null) {
                    product.setInventoryCount(Math.max(0, product.getInventoryCount() - (item.getQuantity() == null ? 0 : item.getQuantity())));
                    productRepository.save(product);
                }
            });
        }

        cart.getItems().clear();
        cartRepository.save(cart);
        return orderRepository.save(order);
    }

    @Override
    public Page<Orders> list(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    private String toBuyerContent(CheckoutRequest req) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(req);
        } catch (Exception e) {
            return null;
        }
    }

    private OrderStatusEnum mapStatus(String status) {
        if (status == null) {
            return OrderStatusEnum.PENDING;
        }
        return switch (status.toLowerCase()) {
            case "paid" -> OrderStatusEnum.PAID;
            case "completed" -> OrderStatusEnum.COMPLETED;
            case "cancelled" -> OrderStatusEnum.CANCELLED;
            case "refunded" -> OrderStatusEnum.REFUNDED;
            default -> OrderStatusEnum.PENDING;
        };
    }
}
