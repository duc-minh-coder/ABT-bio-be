package com.NMCNPM.ABT_bio.repository;

import com.NMCNPM.ABT_bio.entity.PaymentTransactions;
import com.NMCNPM.ABT_bio.enums.PaymentStatusEnum;
import com.NMCNPM.ABT_bio.enums.PaymentTypeEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactions, UUID> {
    Optional<PaymentTransactions> findByTransactionCode(String transactionCode);

    Optional<PaymentTransactions> findByOrderId(UUID orderId);

    boolean existsByTransactionCode(String transactionCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentTransactions p where p.id = :id")
    Optional<PaymentTransactions> findByIdForUpdate(@Param("id") UUID id);

//    Optional<PaymentTransactions> findByReference(String reference);

    Optional<PaymentTransactions> findByTransactionCodeProvider(String providerId);

    Optional<PaymentTransactions> findByProviderEventId(String payosOrderCode);

    Page<PaymentTransactions> findByUserIdAndType(UUID userId, PaymentTypeEnum type, Pageable pageable);

    Optional<PaymentTransactions> findByPayoutBatchId(String payoutBatchId);

    List<PaymentTransactions> findAllByStatusAndTypeAndCreatedAtBefore(
            PaymentStatusEnum status,
            PaymentTypeEnum type,
            Instant time
    );

    // Thêm method này để lấy transaction theo trạng thái (dành cho Admin)
    Page<PaymentTransactions> findAllByStatus(PaymentStatusEnum status, Pageable pageable);

    // Method lấy transaction của user cụ thể (dành cho Seller history)
    Page<PaymentTransactions> findAllByUserId(UUID userId, Pageable pageable);

    Page<PaymentTransactions> findByTypeAndStatus(PaymentTypeEnum type, PaymentStatusEnum status, Pageable pageable);

    @Query("SELECT p FROM PaymentTransactions p WHERE p.status = :status AND p.type IN :types AND p.createdAt < :timeLimit")
    List<PaymentTransactions> findExpiredPendingTransactions(
            @Param("status") PaymentStatusEnum status,
            @Param("types") List<PaymentTypeEnum> types,
            @Param("timeLimit") Instant timeLimit
    );
}
