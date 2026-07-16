package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.dto.response.UserResponse;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.exception.AppException;
import com.NMCNPM.ABT_bio.exception.ErrorCode;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.UserService;
import com.NMCNPM.ABT_bio.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Page<Users> list(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Users get(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserResponse getMyInfo() {
        UUID userId = SecurityUtils.getCurrentUserId();

        Users user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .contactEmail(user.getContactEmail())
                .contactPhone(user.getContactPhone())
                .avatarUrl(user.getAvatarUrl())
                .lastTimeChange(user.getLastTimeChange())
                .verified(user.isVerified())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .role(user.getRole())
                .build();
    }
}
