package com.seth.backend.subject.dto;

public record SubjectResponse(
        Long id,
        String code,
        String nameKm,
        String nameEn,
        boolean active
) {}