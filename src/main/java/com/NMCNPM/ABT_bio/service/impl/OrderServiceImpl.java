package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.dto.request.CheckoutRequest;
import com.NMCNPM.ABT_bio.entity.*;
import com.NMCNPM.ABT_bio.enums.OrderStatusEnum;
import com.NMCNPM.ABT_bio.exception.AppException;
import com.NMCNPM.ABT_bio.exception.ErrorCode;
import com.NMCNPM.ABT_bio.repository.CartRepository;
import com.NMCNPM.ABT_bio.repository.OrderRepository;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.NMCNPM.ABT_bio.enums.OrderStatusEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Transactional
    public void markOrderAsPaid(PaymentTransactions tx) {
        // 1. Tìm đơn hàng dựa trên giao dịch thanh toán
        Orders order = orderRepository.findByPaymentTransaction(tx)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_TX_NOTFOUND));

        // 2. Nếu đơn hàng đã bị huỷ trước đó (VD: quá hạn thanh toán)
        if (order.getStatus() == OrderStatusEnum.CANCELLED) {
            log.warn("CẢNH BÁO: Khách hàng thanh toán thành công nhưng đơn hàng ĐÃ HUỶ. OrderCode: {}", order.getOrderCode());
            return;
        }

        // 3. Nếu đơn hàng đã được thanh toán rồi thì bỏ qua (tránh Webhook gọi trùng nhiều lần)
        if (order.getStatus() == OrderStatusEnum.PAID || order.getStatus() == OrderStatusEnum.COMPLETED) {
            log.info("Đơn hàng {} đã được ghi nhận thanh toán trước đó.", order.getOrderCode());
            return;
        }

        // 4. Kiểm tra số tiền khách chuyển có đủ với giá trị đơn hàng không
        if (tx.getAmount().compareTo(order.getTotalAmount()) < 0) {
            log.error("CẢNH BÁO: Thanh toán thiếu tiền cho đơn hàng {}. Cần: {}, Thực nhận: {}",
                    order.getOrderCode(), order.getTotalAmount(), tx.getAmount());
            // Có thể đổi status sang một trạng thái như PAYMENT_INCOMPLETE hoặc quăng lỗi
            return;
        }

        // 5. Cập nhật trạng thái đơn hàng thành ĐÃ THANH TOÁN
        order.setStatus(OrderStatusEnum.PAID);

        // (Tuỳ chọn) Cập nhật thêm ghi chú giao hàng nếu cần
        order.setDeliveryContent("Đã thanh toán qua PayOS. Đang chờ giao hàng.");

        // 6. Lưu lại vào Database
        orderRepository.save(order);

        log.info("✅ Đơn hàng {} đã được thanh toán thành công!", order.getOrderCode());
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
            case "paid" -> PAID;
            case "completed" -> COMPLETED;
            case "cancelled" -> CANCELLED;
            case "refunded" -> OrderStatusEnum.REFUNDED;
            default -> OrderStatusEnum.PENDING;
        };
    }
}
