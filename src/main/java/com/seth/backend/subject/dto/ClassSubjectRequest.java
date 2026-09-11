package com.seth.backend.subject.dto;

import jakarta.validation.constraints.NotNull;

public record ClassSubjectRequest(
        @NotNull Long subjectId
) {}