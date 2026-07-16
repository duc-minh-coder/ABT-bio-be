package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.dto.request.AddCartRequest;
import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.exception.AppException;
import com.NMCNPM.ABT_bio.exception.ErrorCode;
import com.NMCNPM.ABT_bio.repository.CartRepository;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public Cart getCartByUserId(UUID userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return cartRepository.findByUser(user).orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    @Override
    public Cart addToCart(UUID userId, AddCartRequest req) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder().user(user).build());
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        int quantityToAdd = req.getQuantity() != null ? req.getQuantity() : 1;

        // KIỂM TRA SẢN PHẨM ĐÃ TỒN TẠI TRONG GIỎ HÀNG CHƯA
        java.util.Optional<Cart.CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Đã có trong giỏ -> Cộng dồn số lượng
            Cart.CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantityToAdd);
        } else {
            // Chưa có trong giỏ -> Thêm item mới
            Cart.CartItem newItem = Cart.CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(quantityToAdd)
                    .unitPrice(product.getPrices().isEmpty() ? null : product.getPrices().get(0).getAmount())
                    .currency(product.getPrices().isEmpty() ? null : product.getPrices().get(0).getCurrency().name())
                    .build();
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItem(UUID userId, int itemIndex) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        cart.getItems().remove(itemIndex);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemByProductId(UUID userId, Long productId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        cart.getItems().removeIf(item -> productId.equals(item.getProductId()));
        return cartRepository.save(cart);
    }
}
