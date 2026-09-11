package com.seth.backend.student.dto;

import jakarta.validation.constraints.NotNull;

public record StudentEnrollmentRequest(
        @NotNull Long studentId,
        @NotNull Long schoolClassId
) {}