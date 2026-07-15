package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.request.AuthenticationRequest;
import com.NMCNPM.ABT_bio.dto.request.RegisterRequest;
import com.NMCNPM.ABT_bio.dto.response.AuthenticationResponse;
import com.NMCNPM.ABT_bio.service.AuthenticationService;
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

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        var resp = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().code(0).result(resp).build();
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request) {
        authenticationService.register(request);
        return ApiResponse.<Void>builder().code(0).message("OK").build();
    }
}
