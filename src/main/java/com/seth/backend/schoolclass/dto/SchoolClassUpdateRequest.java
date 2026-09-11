package com.seth.backend.schoolclass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SchoolClassUpdateRequest(
        @NotBlank @Size(max = 10) String name,
        Long studyTrackId,
        @Positive Short capacity
) {}