package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.dto.request.AddCartRequest;

import java.util.UUID;

public interface CartService {
    Cart getCartByUserId(UUID userId);
    Cart addToCart(UUID userId, AddCartRequest req);
    Cart removeItem(UUID userId, int itemIndex);
    Cart removeItemByProductId(UUID userId, Long productId);
}
