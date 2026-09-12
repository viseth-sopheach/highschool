package com.seth.backend.academic.dto;

import java.math.BigDecimal;

public record GradingScaleResponse(
        Long id,
        BigDecimal minScore,
        BigDecimal maxScore,
        String letterGrade,
        BigDecimal gpaPoint,
        String description
) {}