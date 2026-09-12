package com.seth.backend.assignment.dto;

import jakarta.validation.constraints.NotNull;

public record ClassTeacherAssignmentRequest(
        @NotNull Long teacherId,
        Long subjectId,
        boolean homeroom
) {}