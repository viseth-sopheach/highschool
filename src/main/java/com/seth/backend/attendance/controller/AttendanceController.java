package com.seth.backend.attendance.controller;

import com.seth.backend.attendance.dto.AttendanceBulkMarkRequest;
import com.seth.backend.attendance.dto.AttendanceMarkRequest;
import com.seth.backend.attendance.dto.AttendanceResponse;
import com.seth.backend.attendance.service.AttendanceService;
import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.security.PermissionConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

   private final AttendanceService attendanceService;

   @GetMapping("/by-student/{studentId}")
   @PreAuthorize(PermissionConstants.ATTENDANCE_READ_ANY_OR_OWN)
   public PageResponse<AttendanceResponse> listForStudent(
           @PathVariable Long studentId,
           @PageableDefault(size = 30) Pageable pageable) {
      attendanceService.assertOwnedByCurrentUser(studentId);
      return PageResponse.from(attendanceService.listForStudent(studentId, PageableUtils.capped(pageable)));
   }

   @GetMapping("/by-class/{schoolClassId}")
   @PreAuthorize(PermissionConstants.ATTENDANCE_READ)
   public List<AttendanceResponse> listForClassOnDate(
           @PathVariable Long schoolClassId,
           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
      return attendanceService.listForClassOnDate(schoolClassId, date);
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.ATTENDANCE_WRITE)
   public AttendanceResponse mark(@Valid @RequestBody AttendanceMarkRequest request) {
      return attendanceService.mark(request);
   }

   @PostMapping("/bulk")
   @PreAuthorize(PermissionConstants.ATTENDANCE_WRITE)
   public List<AttendanceResponse> markBulk(@Valid @RequestBody AttendanceBulkMarkRequest request) {
      return attendanceService.markBulk(request);
   }
}