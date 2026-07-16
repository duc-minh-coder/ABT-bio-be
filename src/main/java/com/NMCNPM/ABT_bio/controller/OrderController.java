package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.ApiResponse;
import com.NMCNPM.ABT_bio.dto.request.CheckoutRequest;
import com.NMCNPM.ABT_bio.dto.response.OrderResponse;
import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.enums.OrderStatusEnum;
import com.NMCNPM.ABT_bio.service.ApiContractMapper;
import com.NMCNPM.ABT_bio.service.OrderService;
import com.NMCNPM.ABT_bio.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ApiContractMapper apiContractMapper;

    @PostMapping("/cart/checkout")
    public ApiResponse<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest req) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Orders order = orderService.checkout(userId, req);
        return ApiResponse.<OrderResponse>builder().code(0).result(apiContractMapper.toOrderResponse(order)).message("Checkout created").build();
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> listMyOrders(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size) {
        Page<Orders> p = orderService.list(PageRequest.of(page, size));
        List<OrderResponse> mapped = p.getContent().stream().map(apiContractMapper::toOrderResponse).collect(Collectors.toList());
        return ApiResponse.<List<OrderResponse>>builder().code(0).result(mapped).build();
    }

    // API 1: Dành cho User thường (Chỉ lấy đơn của mình)
    @GetMapping("/my-completed")
    public ApiResponse<List<OrderResponse>> getMyCompletedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID userId = SecurityUtils.getCurrentUserId();
        List<OrderStatusEnum> statuses = List.of(OrderStatusEnum.PAID, OrderStatusEnum.COMPLETED);

        Page<Orders> p = orderService.findOrdersByBuyer(userId, statuses, PageRequest.of(page, size));

        return ApiResponse.<List<OrderResponse>>builder()
                .code(0)
                .result(p.getContent().stream().map(apiContractMapper::toOrderResponse).toList())
                .build();
    }

    // API 2: Dành cho Admin (Lấy tất cả đơn đã hoàn thành của mọi người)
    @GetMapping("/admin/completed")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới vào được
    public ApiResponse<List<OrderResponse>> getAllCompletedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<OrderStatusEnum> statuses = List.of(OrderStatusEnum.PAID, OrderStatusEnum.COMPLETED);

        Page<Orders> p = orderService.findAllOrdersByStatus(statuses, PageRequest.of(page, size));

        return ApiResponse.<List<OrderResponse>>builder()
                .code(0)
                .result(p.getContent().stream().map(apiContractMapper::toOrderResponse).toList())
                .build();
    }
}
