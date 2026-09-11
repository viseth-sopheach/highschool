package com.seth.backend.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentCreateRequest(
        @NotNull Long userId,
        @NotBlank @Size(max = 20) String studentCode,
        @NotBlank @Size(max = 150) String khmerName,
        @Size(max = 150) String englishName,
        LocalDate dob,
        @Size(max = 10) String gender,
        @Size(max = 20) String phone
) {}