package com.seth.backend.schoolclass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SchoolClassCreateRequest(
        @NotNull Long academicYearId,
        @NotNull Long gradeId,
        Long studyTrackId,
        @NotBlank @Size(max = 10) String name,
        @Positive Short capacity
) {}