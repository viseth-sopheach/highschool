package com.seth.backend.schoolclass.dto;

public record SchoolClassResponse(
        Long id,
        Long academicYearId,
        String academicYearName,
        Long gradeId,
        String gradeName,
        Long studyTrackId,
        String studyTrackName,
        String name,
        Short capacity
) {}