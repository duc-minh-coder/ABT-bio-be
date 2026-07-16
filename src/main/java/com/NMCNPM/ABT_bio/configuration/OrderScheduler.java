package com.NMCNPM.ABT_bio.configuration;

import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.enums.OrderStatusEnum;
import com.NMCNPM.ABT_bio.repository.OrderRepository;
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
            log.info("Đang hủy {} đơn hàng quá hạn...", pendingOrders.size());

            for (Orders order : pendingOrders) {
                order.setStatus(OrderStatusEnum.CANCELLED);
                // Lưu lại
                orderRepository.save(order);
                log.info("Đã hủy đơn hàng: {}", order.getOrderCode());
            }
        }
    }
}