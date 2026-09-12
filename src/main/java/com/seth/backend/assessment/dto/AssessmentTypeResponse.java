package com.seth.backend.assessment.dto;

import java.math.BigDecimal;

public record AssessmentTypeResponse(Long id, String name, BigDecimal weightDefault) {}