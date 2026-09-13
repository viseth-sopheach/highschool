package com.seth.backend.user.dto;

import com.seth.backend.user.entity.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @NotNull UserStatus status
) {}