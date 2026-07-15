package com.NMCNPM.ABT_bio.repository;

import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUser(Users user);
}
