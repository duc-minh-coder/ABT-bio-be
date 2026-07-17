package com.NMCNPM.ABT_bio.configuration;

import com.NMCNPM.ABT_bio.entity.OrderItem;
import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.enums.OrderStatusEnum;
import com.NMCNPM.ABT_bio.repository.OrderRepository;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    // Chạy mỗi 1 phút (60000ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCancelPendingOrders() {
        // Lấy thời điểm cách đây 5 phút
        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);

        // Tìm tất cả đơn hàng PENDING tạo trước 5 phút
        List<Orders> pendingOrders = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatusEnum.PENDING,
                fiveMinutesAgo
        );

        if (!pendingOrders.isEmpty()) {
            log.info("Đang hủy {} đơn hàng quá hạn và hoàn trả tồn kho...", pendingOrders.size());

            for (Orders order : pendingOrders) {
                // HỒN TRẢ SỐ LƯỢNG SẢN PHẨM VÀO KHO
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();
                    if (product != null) {
                        int currentStock = product.getInventoryCount() != null ? product.getInventoryCount() : 0;
                        product.setInventoryCount(currentStock + item.getQuantity());
                        productRepository.save(product);
                        log.info("  ↳ Hoàn trả {} x sản phẩm ID: {} ({})",
                                item.getQuantity(), product.getId(), product.getName());
                    }
                }

                // Đổi status đơn hàng
                order.setStatus(OrderStatusEnum.CANCELLED);
                orderRepository.save(order);
                log.info("✅ Đã hủy đơn hàng: {}", order.getOrderCode());
            }
        }
    }
}