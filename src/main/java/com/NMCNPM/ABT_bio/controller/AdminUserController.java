package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.response.UserResponse;
import com.NMCNPM.ABT_bio.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.NMCNPM.ABT_bio.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {
    private final com.NMCNPM.ABT_bio.service.UserService userService;

    @GetMapping("/users")
    public ApiResponse<List<UserResponse>> listUsers(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        Page<Users> p = userService.list(PageRequest.of(page, size));
        List<UserResponse> users = p.getContent().stream().map(this::toUserResponse).toList();
        return ApiResponse.<List<UserResponse>>builder().code(0).result(users).build();
    }

    @GetMapping("/users/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable String id) {
        var u = userService.get(java.util.UUID.fromString(id));
        if (u == null) return ApiResponse.<UserResponse>builder().code(1).message("Not found").build();
        return ApiResponse.<UserResponse>builder().code(0).result(toUserResponse(u)).build();
    }

    private UserResponse toUserResponse(Users user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .contactEmail(user.getContactEmail())
                .contactPhone(user.getContactPhone())
                .fullName(user.getFullName())
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
