package com.NMCNPM.ABT_bio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_webhooks",
        indexes = {@Index(name = "idx_provider_event_id", columnList = "provider_event_id")}
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentWebhook {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    String provider;

    @Column(name = "provider_event_id", unique = true)
    String providerEventId;

    @Lob
    @Column(columnDefinition = "TEXT")
    String payload;

    @CreationTimestamp
    Instant receivedAt;

    @Column(nullable = false)
    boolean processed = false;

    @Column(name = "payment_tx_id")
    UUID paymentTxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @JsonIgnore
    PaymentTransactions payment;
}
