package com.seth.backend.user.dto;

import com.seth.backend.user.entity.UserStatus;

import java.time.OffsetDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserStatus status,
        OffsetDateTime lastLoginAt,
        OffsetDateTime createdAt,
        Set<String> roles
) {}