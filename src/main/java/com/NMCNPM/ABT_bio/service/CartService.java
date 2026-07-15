package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.dto.request.AddCartRequest;

public interface CartService {
    Cart getCartByUserEmail(String email);
    Cart addToCart(String email, AddCartRequest req);
    Cart removeItem(String email, int itemIndex);
}
