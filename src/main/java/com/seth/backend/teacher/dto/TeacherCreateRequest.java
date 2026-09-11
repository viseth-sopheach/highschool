package com.seth.backend.teacher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * {@code userId} must reference an existing user account (created via the
 * future user-management endpoint) — there's no inline user creation here
 * on purpose, so identity/auth concerns stay owned by the user module.
 */
public record TeacherCreateRequest(
        @NotNull Long userId,
        @NotBlank @Size(max = 20) String teacherCode,
        @NotBlank @Size(max = 150) String khmerName,
        @Size(max = 150) String englishName,
        @Size(max = 20) String phone,
        LocalDate hireDate
) {}