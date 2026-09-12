package com.seth.backend.attendance.dto;

import com.seth.backend.attendance.entity.AttendanceStatus;

import java.time.LocalDate;

public record AttendanceResponse(
        Long id,
        Long studentId,
        String studentName,
        Long schoolClassId,
        String schoolClassName,
        LocalDate attendanceDate,
        AttendanceStatus status,
        String remarks
) {}