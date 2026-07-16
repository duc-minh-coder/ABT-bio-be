package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.ApiResponse;
import com.NMCNPM.ABT_bio.dto.response.UserResponse;
import com.NMCNPM.ABT_bio.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class UserController {
    UserService userService;
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }
}
