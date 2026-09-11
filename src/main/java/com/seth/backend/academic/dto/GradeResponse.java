package com.seth.backend.academic.dto;

public record GradeResponse(
        Long id,
        String name,
        short level,
        boolean requiresTrack
) {}