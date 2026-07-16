package com.NMCNPM.ABT_bio.dto.response;

import com.NMCNPM.ABT_bio.entity.UserIdentity;
import com.NMCNPM.ABT_bio.enums.RoleEnum;
import com.NMCNPM.ABT_bio.enums.UserStatusEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class UserResponse {
    UUID id;

    String contactEmail;

    String contactPhone;

    String fullName;

    String avatarUrl;

    Instant lastTimeChange;

    boolean verified;

    UserStatusEnum status;

    Instant createdAt;

    Instant updatedAt;

    RoleEnum role;
}
