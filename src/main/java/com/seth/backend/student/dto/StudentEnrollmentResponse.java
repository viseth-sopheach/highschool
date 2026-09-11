package com.seth.backend.student.dto;

import com.seth.backend.student.entity.EnrollmentStatus;

import java.time.LocalDate;

public record StudentEnrollmentResponse(
        Long id,
        Long studentId,
        String studentCode,
        Long schoolClassId,
        String schoolClassName,
        Long academicYearId,
        String academicYearName,
        LocalDate enrollmentDate,
        EnrollmentStatus status
) {}