package com.NMCNPM.ABT_bio.entity;

import com.NMCNPM.ABT_bio.enums.PaymentMethodEnum;
import com.NMCNPM.ABT_bio.enums.PaymentProviderEnum;
import com.NMCNPM.ABT_bio.enums.PaymentStatusEnum;
import com.NMCNPM.ABT_bio.enums.PaymentTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "payment_transactions",
        indexes = {
//                @Index(name = "idx_payment_order_id", columnList = "order_id"),
                @Index(name = "idx_payment_user_id", columnList = "user_id"),
                @Index(name = "idx_payment_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_provider_txn", columnNames = {"provider", "transaction_code_provider"})
        }
)
public class PaymentTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    PaymentTypeEnum type; // DEPOSIT hoặc ORDER_PAYMENT

    @Column(name = "transaction_code", nullable = false, unique = true, length = 255)
    String transactionCode;

    @Column(name = "transaction_code_provider", length = 255)
    String transactionCodeProvider;

    // Batch ID dùng cho Payout (Rút tiền), PayPal sẽ trả về mã Batch khi rút nhiều lệnh
    @Column(name = "payout_batch_id", length = 255)
    String payoutBatchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    PaymentProviderEnum provider;

    @Column(name = "provider_event_id", length = 255)
    String providerEventId;

    @Column(name = "provider_status", length = 50)
    String providerStatus;

    @Column(nullable = false, precision = 19, scale = 2)
    BigDecimal amount;

    @Column(nullable = false, length = 10)
    String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    PaymentMethodEnum paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    PaymentStatusEnum status;

    @Lob
    @Column(name = "webhook_log", columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String webhookLog;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;


    // Mapping ngược lại Order hệ thống
    @OneToOne(mappedBy = "paymentTransaction")
    @JsonIgnore
    Orders order;
}
