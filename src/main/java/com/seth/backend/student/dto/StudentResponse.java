package com.seth.backend.student.dto;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        Long userId,
        String username,
        String studentCode,
        String khmerName,
        String englishName,
        LocalDate dob,
        String gender,
        String phone
) {}