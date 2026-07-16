package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.repository.CartRepository;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceImplTest {

    private final CartRepository cartRepository = Mockito.mock(CartRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
    private final CartServiceImpl cartService = new CartServiceImpl(cartRepository, userRepository, productRepository);

    @Test
    void removeItemByProductId_removesMatchingItemAndSavesCart() {
        UUID userId = UUID.randomUUID();
        Users user = Users.builder().id(userId).build();
        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>(
                        java.util.List.of(
                                Cart.CartItem.builder().productId(1L).quantity(1).build(),
                                Cart.CartItem.builder().productId(2L).quantity(2).build()
                        )))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Cart updatedCart = cartService.removeItemByProductId(userId, 2L);

        assertThat(updatedCart.getItems()).hasSize(1);
        assertThat(updatedCart.getItems().get(0).getProductId()).isEqualTo(1L);
        verify(cartRepository).save(cart);
    }
}
