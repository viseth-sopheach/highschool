package com.seth.backend.attendance.dto;

import com.seth.backend.attendance.entity.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/** Marks attendance for an entire class roster in a single request/transaction. */
public record AttendanceBulkMarkRequest(
        @NotNull Long schoolClassId,
        @NotNull LocalDate attendanceDate,
        @NotEmpty @Valid List<Entry> entries
) {
   public record Entry(
           @NotNull Long studentId,
           @NotNull AttendanceStatus status,
           String remarks
   ) {}
}