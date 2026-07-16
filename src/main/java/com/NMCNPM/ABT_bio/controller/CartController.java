package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.ApiResponse;
import com.NMCNPM.ABT_bio.dto.request.AddCartRequest;
import com.NMCNPM.ABT_bio.dto.response.CartItemResponse;
import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.service.ApiContractMapper;
import com.NMCNPM.ABT_bio.service.CartService;
import com.NMCNPM.ABT_bio.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ApiContractMapper apiContractMapper;

    @GetMapping("/cart")
    public ApiResponse<List<CartItemResponse>> getCart() {
        UUID userId = SecurityUtils.getCurrentUserId();
        Cart cart = cartService.getCartByUserId(userId);

        List<CartItemResponse> items = apiContractMapper.toCartItemResponses(cart.getItems());
        return ApiResponse.<List<CartItemResponse>>builder().code(0).result(items).build();
    }

    @PostMapping("/cart")
    public ApiResponse<List<CartItemResponse>> addToCart(@RequestBody AddCartRequest req) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Cart cart = cartService.addToCart(userId, req);
        List<CartItemResponse> items = apiContractMapper.toCartItemResponses(cart.getItems());
        return ApiResponse.<List<CartItemResponse>>builder().code(0).result(items).build();
    }

    @DeleteMapping("/cart/{itemIndex}")
    public ApiResponse<List<CartItemResponse>> removeItem(@PathVariable int itemIndex) {
        UUID principal = SecurityUtils.getCurrentUserId();
        Cart cart = cartService.removeItem(principal, itemIndex);
        List<CartItemResponse> items = apiContractMapper.toCartItemResponses(cart.getItems());
        return ApiResponse.<List<CartItemResponse>>builder().code(0).result(items).build();
    }

    @DeleteMapping("/cart/product/{productId}")
    public ApiResponse<List<CartItemResponse>> removeItemByProductId(@PathVariable Long productId) {
        UUID principal = SecurityUtils.getCurrentUserId();
        Cart cart = cartService.removeItemByProductId(principal, productId);
        List<CartItemResponse> items = apiContractMapper.toCartItemResponses(cart.getItems());
        return ApiResponse.<List<CartItemResponse>>builder().code(0).result(items).build();
    }
}
