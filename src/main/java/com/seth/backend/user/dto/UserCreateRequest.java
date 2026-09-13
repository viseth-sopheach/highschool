package com.seth.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Admin-issued account creation. {@code roleNames} must match existing
 * {@code roles.name} values (STUDENT/TEACHER/VICE_PRINCIPAL/PRINCIPAL) —
 * the fixed role catalog is seeded in V1, not user-manageable via API.
 */
public record UserCreateRequest(
        @NotBlank @Size(max = 100) String username,
        @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotEmpty Set<String> roleNames
) {}