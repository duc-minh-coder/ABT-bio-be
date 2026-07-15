package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.dto.request.CheckoutItemRequest;
import com.NMCNPM.ABT_bio.dto.request.CheckoutRequest;
import com.NMCNPM.ABT_bio.dto.response.OrderItemResponse;
import com.NMCNPM.ABT_bio.dto.response.OrderResponse;
import com.NMCNPM.ABT_bio.dto.response.ProductResponse;
import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiContractMapper {
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public ProductResponse toProductResponse(Product product) {
        if (product == null) {
            return null;
        }

        var price = product.getPrices() != null && !product.getPrices().isEmpty()
                ? product.getPrices().get(0).getAmount()
                : BigDecimal.ZERO;

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory() != null ? product.getCategory().getName() : null)
                .price(price)
                .unit("thiết bị")
                .image(product.getThumbnailUrl())
                .description(product.getDetailedDescription())
                .specs(product.getGalleryUrls() == null ? Collections.emptyList() : product.getGalleryUrls())
                .stock(product.getInventoryCount())
                .featured(Boolean.TRUE.equals(product.getIsPopular()))
                .slug(product.getSlug())
                .build();
    }

    public OrderResponse toOrderResponse(Orders order) {
        if (order == null) {
            return null;
        }

        CheckoutRequest payload = parseBuyerContent(order.getBuyerContent());
        String status = payload != null && payload.getStatus() != null ? payload.getStatus() : mapStatus(order.getStatus());
        String paymentStatus = payload != null && payload.getPaymentStatus() != null ? payload.getPaymentStatus() : ("PAID".equalsIgnoreCase(String.valueOf(order.getStatus())) ? "paid" : "unpaid");

        List<OrderItemResponse> items = payload != null && payload.getItems() != null
                ? payload.getItems().stream().map(this::toOrderItemResponse).collect(Collectors.toList())
                : List.of();

        return OrderResponse.builder()
                .id(order.getOrderCode())
                .date(order.getCreatedAt())
                .customerName(payload != null ? payload.getCustomerName() : null)
                .email(payload != null ? payload.getEmail() : null)
                .phone(payload != null ? payload.getPhone() : null)
                .address(payload != null ? payload.getAddress() : null)
                .organization(payload != null ? payload.getOrganization() : null)
                .paymentMethod(payload != null ? payload.getPaymentMethod() : null)
                .items(items)
                .status(status)
                .total(order.getTotalAmount())
                .paymentStatus(paymentStatus)
                .notes(payload != null ? payload.getNotes() : null)
                .build();
    }

    public CheckoutRequest toCheckoutRequest(Orders order) {
        return parseBuyerContent(order.getBuyerContent());
    }

    public List<OrderItemResponse> toOrderItemResponses(List<CheckoutItemRequest> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(this::toOrderItemResponse).collect(Collectors.toList());
    }

    private OrderItemResponse toOrderItemResponse(CheckoutItemRequest item) {
        Product product = productRepository.findById(item.getProductId()).orElse(null);
        return OrderItemResponse.builder()
                .product(toProductResponse(product))
                .quantity(item.getQuantity())
                .priceAtOrder(item.getPriceAtOrder())
                .build();
    }

    private CheckoutRequest parseBuyerContent(String buyerContent) {
        if (buyerContent == null || buyerContent.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(buyerContent, CheckoutRequest.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String mapStatus(com.NMCNPM.ABT_bio.enums.OrderStatusEnum status) {
        if (status == null) {
            return "pending_payment";
        }
        return switch (status) {
            case PAID -> "paid";
            case COMPLETED -> "completed";
            case CANCELLED -> "cancelled";
            case REFUNDED -> "cancelled";
            default -> "pending_payment";
        };
    }
}
