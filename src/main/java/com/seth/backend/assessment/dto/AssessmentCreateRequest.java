package com.seth.backend.assessment.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssessmentCreateRequest(
        @NotNull Long schoolClassId,
        @NotNull Long subjectId,
        @NotNull Long assessmentTypeId,
        @NotBlank @Size(max = 150) String title,
        @NotNull @Positive BigDecimal maxScore,
        BigDecimal weight,
        @NotNull LocalDate assessmentDate
) {}