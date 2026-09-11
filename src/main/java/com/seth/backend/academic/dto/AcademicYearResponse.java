package com.seth.backend.academic.dto;

import java.time.LocalDate;

public record AcademicYearResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        boolean current
) {}