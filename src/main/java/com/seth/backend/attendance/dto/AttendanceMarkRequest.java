package com.seth.backend.attendance.dto;

import com.seth.backend.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AttendanceMarkRequest(
        @NotNull Long studentId,
        @NotNull Long schoolClassId,
        @NotNull LocalDate attendanceDate,
        @NotNull AttendanceStatus status,
        @Size(max = 255) String remarks
) {}