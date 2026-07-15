package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.dto.request.AddCartRequest;
import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.repository.CartRepository;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public Cart getCartByUserEmail(String email) {
        Users user = userRepository.findByContactEmail(email).orElseThrow();
        return cartRepository.findByUser(user).orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    @Override
    public Cart addToCart(String email, AddCartRequest req) {
        Users user = userRepository.findByContactEmail(email).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> Cart.builder().user(user).build());
        Product product = productRepository.findById(req.getProductId()).orElseThrow();
        Cart.CartItem item = Cart.CartItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(req.getQuantity())
                .unitPrice(product.getPrices().isEmpty() ? null : product.getPrices().get(0).getAmount())
                .currency(product.getPrices().isEmpty() ? null : product.getPrices().get(0).getCurrency().name())
                .build();
        cart.getItems().add(item);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItem(String email, int itemIndex) {
        Users user = userRepository.findByContactEmail(email).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseThrow();
        cart.getItems().remove(itemIndex);
        return cartRepository.save(cart);
    }
}
