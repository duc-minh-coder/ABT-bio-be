package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<Users> list(Pageable pageable);
    Users get(UUID id);
}
