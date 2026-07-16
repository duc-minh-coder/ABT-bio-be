package com.NMCNPM.ABT_bio.controller;

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
    public ApiResponse<List<Users>> listUsers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        Page<Users> p = userService.list(PageRequest.of(page, size));
        return ApiResponse.<List<Users>>builder().code(0).result(p.getContent()).build();
    }

    @GetMapping("/users/{id}")
    public ApiResponse<Users> getUser(@PathVariable String id) {
        var u = userService.get(java.util.UUID.fromString(id));
        if (u == null) return ApiResponse.<Users>builder().code(1).message("Not found").build();
        return ApiResponse.<Users>builder().code(0).result(u).build();
    }
}
