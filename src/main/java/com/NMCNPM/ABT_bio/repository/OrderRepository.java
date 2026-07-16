package com.NMCNPM.ABT_bio.repository;

import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.entity.PaymentTransactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Orders, UUID> {
    Page<Orders> findAll(Pageable pageable);
    Optional<Orders> findByPaymentTransaction(PaymentTransactions tx);
}
