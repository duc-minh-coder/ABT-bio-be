package com.NMCNPM.ABT_bio.dto.response;

import com.NMCNPM.ABT_bio.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthUserProjectionResponse {
    UUID userId;
    String email;
    String referralCode;

    @JsonIgnore
    String passwordHash;
    String role;
    UserStatusEnum status;
    String fullName;
    String avatarUrl;
    Boolean mustChangePassword;

    String contactEmail;
    UUID shopId;
}
