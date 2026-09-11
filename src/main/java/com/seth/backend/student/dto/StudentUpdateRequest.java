package com.seth.backend.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentUpdateRequest(
        @NotBlank @Size(max = 150) String khmerName,
        @Size(max = 150) String englishName,
        LocalDate dob,
        @Size(max = 10) String gender,
        @Size(max = 20) String phone
) {}