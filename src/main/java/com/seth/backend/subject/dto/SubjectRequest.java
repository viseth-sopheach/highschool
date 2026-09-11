package com.seth.backend.subject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
        @NotBlank @Size(max = 20) String code,
        @NotBlank @Size(max = 100) String nameKm,
        @NotBlank @Size(max = 100) String nameEn,
        boolean active
) {}