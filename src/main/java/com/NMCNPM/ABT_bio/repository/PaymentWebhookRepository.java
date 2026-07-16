package com.NMCNPM.ABT_bio.repository;

import com.NMCNPM.ABT_bio.entity.PaymentWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentWebhookRepository extends JpaRepository<PaymentWebhook, UUID> {
    Boolean existsByProviderEventId(String providerEventId);
}
