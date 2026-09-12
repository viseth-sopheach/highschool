package com.seth.backend.assessment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ScoreEntryRequest(
        @NotNull Long studentEnrollmentId,
        @NotNull @PositiveOrZero BigDecimal score,
        String remarks
) {}