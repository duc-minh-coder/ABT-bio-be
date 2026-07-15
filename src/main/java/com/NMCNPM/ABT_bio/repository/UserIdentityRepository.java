package com.NMCNPM.ABT_bio.repository;

import com.NMCNPM.ABT_bio.dto.response.AuthUserProjectionResponse;
import com.NMCNPM.ABT_bio.entity.UserIdentity;
import com.NMCNPM.ABT_bio.enums.IdentityProviderEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, UUID> {
    boolean existsByProviderAndProviderUserId(
            IdentityProviderEnum provider,
            String providerUserId
    );

    Optional<UserIdentity> findByProviderAndProviderUserId(
            IdentityProviderEnum provider,
            String providerUserId
    );

    // ===================== LOCAL LOGIN =====================
    @Query("""
        SELECT new com.NMCNPM.ABT_bio.dto.response.AuthUserProjectionResponse(
            user.id,
            userIdentity.providerUserId,
            CAST(NULL AS string),
            user.role,
            user.status,
            user.fullName,
            user.avatarUrl,
            user.contactEmail
        )
        FROM UserIdentity userIdentity
        JOIN userIdentity.user user
        WHERE userIdentity.providerUserId = :email
          AND userIdentity.provider = com.NMCNPM.ABT_bio.enums.IdentityProviderEnum.LOCAL
    """)
    Optional<AuthUserProjectionResponse> findLocalAuthUserByEmail(
            @Param("email") String email
    );

    @Query("""
        SELECT new com.NMCNPM.ABT_bio.dto.response.AuthUserProjectionResponse(
            user.id,
            userIdentity.providerUserId,
            CAST(NULL AS string),
            user.role,
            user.status,
            user.fullName,
            user.avatarUrl,
            user.contactEmail
        )
        FROM UserIdentity userIdentity
        JOIN userIdentity.user user
        WHERE userIdentity.providerUserId = :email
    """)
    Optional<AuthUserProjectionResponse> findAuthUserByEmail(
            @Param("email") String email
    );

    /**
     * Find UserIdentity by user ID
     */
    Optional<UserIdentity> findByUserId(UUID userId);

    /**
     * Find all UserIdentity by user
     */
    List<UserIdentity> findAllByUserId(UUID userId);
}
