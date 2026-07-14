package com.NMCNPM.ABT_bio.entity;

import com.NMCNPM.ABT_bio.enums.RoleEnum;
import com.NMCNPM.ABT_bio.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "contact_email"),
                @Index(name = "idx_users_phone", columnList = "contact_phone")
        }
)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    // Email dùng để liên hệ / invoice. Không dùng làm source xác thực trực tiếp
    @Column(name = "contact_email")
    String contactEmail;

    @Column(name = "contact_phone")
    String contactPhone;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    String avatarUrl;

    @Column(name = "last_time_change", columnDefinition = "timestamptz")
    @Builder.Default
    Instant lastTimeChange = null;

    // Tài khoản ở mức "account" đã verified (ví dụ: sau email verification)
    @Column(name = "verified")
    @Builder.Default
    boolean verified = false;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    UserStatusEnum status;

    @Column(name = "created_at", columnDefinition = "timestamptz")
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "timestamptz")
    Instant updatedAt;

    // identity
    @OneToOne(mappedBy = "user")
    UserIdentity identity;

    // role
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    RoleEnum role;

    // payment
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    List<PaymentTransactions> paymentTransactionsList = new ArrayList<>();
}
