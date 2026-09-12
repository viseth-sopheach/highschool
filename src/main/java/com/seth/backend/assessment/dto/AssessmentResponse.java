package com.seth.backend.assessment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssessmentResponse(
        Long id,
        Long schoolClassId,
        String schoolClassName,
        Long subjectId,
        String subjectName,
        Long assessmentTypeId,
        String assessmentTypeName,
        String title,
        BigDecimal maxScore,
        BigDecimal weight,
        LocalDate assessmentDate
) {}