package com.seth.backend.teacher.dto;

import java.time.LocalDate;

public record TeacherResponse(
        Long id,
        Long userId,
        String username,
        String teacherCode,
        String khmerName,
        String englishName,
        String phone,
        LocalDate hireDate
) {}