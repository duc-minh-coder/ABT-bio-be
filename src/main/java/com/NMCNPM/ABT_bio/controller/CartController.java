package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.ApiResponse;
import com.NMCNPM.ABT_bio.dto.request.AddCartRequest;
import com.NMCNPM.ABT_bio.dto.response.CartItemResponse;
import com.NMCNPM.ABT_bio.dto.response.ProductResponse;
import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.service.ApiContractMapper;
import com.NMCNPM.ABT_bio.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ApiContractMapper apiContractMapper;

    @GetMapping("/cart")
    public ApiResponse<List<CartItemResponse>> getCart() {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartService.getCartByUserEmail(principal);
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .product(apiContractMapper.toProductResponse(
                                com.NMCNPM.ABT_bio.entity.Product.builder().id(item.getProductId()).name(item.getProductName()).inventoryCount(0).build()))
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.<List<CartItemResponse>>builder().code(0).result(items).build();
    }

    @PostMapping("/cart")
    public ApiResponse<List<CartItemResponse>> addToCart(@RequestBody AddCartRequest req) {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartService.addToCart(principal, req);
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .product(apiContractMapper.toProductResponse(
                                com.NMCNPM.ABT_bio.entity.Product.builder().id(item.getProductId()).name(item.getProductName()).inventoryCount(0).build()))
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.<List<CartItemResponse>>builder().code(0).result(items).build();
    }

    @DeleteMapping("/cart/{itemIndex}")
    public ApiResponse<List<CartItemResponse>> removeItem(@PathVariable int itemIndex) {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartService.removeItem(principal, itemIndex);
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .product(apiContractMapper.toProductResponse(
                                com.NMCNPM.ABT_bio.entity.Product.builder().id(item.getProductId()).name(item.getProductName()).inventoryCount(0).build()))
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.<List<CartItemResponse>>builder().code(0).result(items).build();
    }
}
