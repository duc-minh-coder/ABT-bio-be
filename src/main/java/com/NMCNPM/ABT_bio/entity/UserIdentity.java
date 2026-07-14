package com.NMCNPM.ABT_bio.entity;

import com.NMCNPM.ABT_bio.enums.IdentityProviderEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
    uniqueConstraints = {
            @UniqueConstraint(columnNames = {"provider", "provider_user_id"})
    }, // MỘT provider + MỘT định danh đăng nhập CHỈ ĐƯỢC TỒN TẠI DUY NHẤT 1 LẦN
    indexes = {
            @Index(name = "idx_user_identities_user_id", columnList = "user_id")
    }
)
public class UserIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    IdentityProviderEnum provider;

    /**
     * Định danh đăng nhập:
     * - username
     * - email
     * - phone
     * - oauth sub
     */
    @Column(name = "provider_user_id", nullable = false)
    String providerUserId;

    @Column(name = "email")
    String email;

    @Column(name = "phone")
    String phone;

    @Column(name = "password_hash")
    String passwordHash;

    @Builder.Default
    @Column(name = "verified")
    Boolean verified = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "timestamptz")
    Instant createdAt;
}
