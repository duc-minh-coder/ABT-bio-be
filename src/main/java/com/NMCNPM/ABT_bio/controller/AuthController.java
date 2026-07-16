package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.request.AuthenticationRequest;
import com.NMCNPM.ABT_bio.dto.request.RegisterRequest;
import com.NMCNPM.ABT_bio.dto.response.AuthenticationResponse;
import com.NMCNPM.ABT_bio.dto.response.UserResponse;
import com.NMCNPM.ABT_bio.service.AuthenticationService;
import com.NMCNPM.ABT_bio.service.UserService;
import com.NMCNPM.ABT_bio.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        var resp = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().code(0).result(resp).build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        var user = userService.getMyInfo();
        return ApiResponse.<UserResponse>builder().code(0).result(user).build();
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request) {
        authenticationService.register(request);
        return ApiResponse.<Void>builder().code(0).message("OK").build();
    }
}
