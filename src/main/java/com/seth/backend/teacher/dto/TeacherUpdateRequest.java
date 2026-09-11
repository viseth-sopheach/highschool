package com.seth.backend.teacher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TeacherUpdateRequest(
        @NotBlank @Size(max = 150) String khmerName,
        @Size(max = 150) String englishName,
        @Size(max = 20) String phone,
        LocalDate hireDate
) {}