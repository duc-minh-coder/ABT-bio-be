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
import java.util.List;
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
    public Orders checkout(UUID userId, CheckoutRequest req) {
        Users user = userRepository.findById(userId).orElseThrow();

        Cart cart = cartRepository.findByUser(user).orElseThrow();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        // Khởi tạo Đơn hàng (chưa có tổng tiền và items)
        Orders order = Orders.builder()
                .orderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .buyer(user)
                .buyerContent(req.getNotes())
                .currency("VND")
                .status(PENDING)
                .createdAt(Instant.now())
                .orderItems(new java.util.ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Quét toàn bộ giỏ hàng để tạo OrderItem và tính tiền
        for (Cart.CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + cartItem.getProductId()));

            int buyQty = cartItem.getQuantity();

            // Kiểm tra tồn kho chặt chẽ
            if (product.getInventoryCount() < buyQty) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng trong kho!");
            }

            // Trừ tồn kho
            product.setInventoryCount(product.getInventoryCount() - buyQty);
            productRepository.save(product);

            // Lấy giá chuẩn xác từ Product DB
            BigDecimal unitPrice = product.getPrices().isEmpty() ? BigDecimal.ZERO : product.getPrices().get(0).getAmount();

            // Tính tổng tiền cho item này và cộng dồn vào tổng hóa đơn
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(buyQty));
            totalAmount = totalAmount.add(itemTotal);

            // Tạo chi tiết đơn hàng
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(buyQty)
                    .unitPrice(unitPrice)
                    .productNameSnapshot(product.getName())
                    .build();

            order.getOrderItems().add(orderItem);
        }

        // Chốt tổng tiền an toàn vào đơn hàng
        order.setTotalAmount(totalAmount);

        // Xóa toàn bộ giỏ hàng
//        cart.getItems().clear();
//        cartRepository.save(cart);

        // Lưu đơn hàng
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

        // 6. XÓA GIỎ HÀNG SAU KHI THANH TOÁN
        Cart cart = cartRepository.findByUser(order.getBuyer()).orElse(null);
        if (cart != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
            log.info("🛒 Đã làm trống giỏ hàng của user sau khi thanh toán thành công.");
        }

        log.info("✅ Đơn hàng {} đã được thanh toán thành công!", order.getOrderCode());
    }

    @Override
    public Page<Orders> listOrdersByStatus(UUID userId, boolean isAdmin, List<OrderStatusEnum> statuses, Pageable pageable) {
        if (isAdmin) {
            return orderRepository.findByStatusIn(statuses, pageable);
        } else {
            return orderRepository.findByBuyerIdAndStatusIn(userId, statuses, pageable);
        }
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
