package com.seth.backend.academic.dto;

public record StudyTrackResponse(
        Long id,
        String code,
        String nameKm,
        String nameEn
) {}