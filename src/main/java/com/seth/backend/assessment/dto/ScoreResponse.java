package com.seth.backend.assessment.dto;

import java.math.BigDecimal;

public record ScoreResponse(
        Long id,
        Long assessmentId,
        String assessmentTitle,
        Long studentEnrollmentId,
        String studentCode,
        BigDecimal score,
        BigDecimal maxScore,
        String letterGrade,
        String remarks
) {}