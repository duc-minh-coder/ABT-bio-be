package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.UserService;
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
}
