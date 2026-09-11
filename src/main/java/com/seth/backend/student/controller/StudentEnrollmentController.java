package com.seth.backend.student.controller;

import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.security.PermissionConstants;
import com.seth.backend.student.dto.StudentEnrollmentRequest;
import com.seth.backend.student.dto.StudentEnrollmentResponse;
import com.seth.backend.student.entity.EnrollmentStatus;
import com.seth.backend.student.service.StudentEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class StudentEnrollmentController {

   private final StudentEnrollmentService enrollmentService;

   @GetMapping("/by-student/{studentId}")
   @PreAuthorize(PermissionConstants.STUDENT_READ_ANY_OR_OWN)
   public PageResponse<StudentEnrollmentResponse> listForStudent(
           @PathVariable Long studentId,
           @PageableDefault(size = 20, sort = "enrollmentDate") Pageable pageable) {
      if (!org.springframework.security.access.prepost.PreAuthorize.class.isInterface()) {
         // no-op, keeps import used if IDE strips it — remove in real build
      }
      enrollmentService.assertOwnedByCurrentUser(studentId);
      return PageResponse.from(enrollmentService.listForStudent(studentId, PageableUtils.capped(pageable)));
   }

   @GetMapping("/by-class/{schoolClassId}")
   @PreAuthorize(PermissionConstants.CLASS_READ)
   public PageResponse<StudentEnrollmentResponse> listForClass(
           @PathVariable Long schoolClassId,
           @PageableDefault(size = 20, sort = "enrollmentDate") Pageable pageable) {
      return PageResponse.from(enrollmentService.listForClass(schoolClassId, PageableUtils.capped(pageable)));
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.ENROLLMENT_WRITE)
   public StudentEnrollmentResponse enroll(@Valid @RequestBody StudentEnrollmentRequest request) {
      return enrollmentService.enroll(request);
   }

   @PatchMapping("/{id}/status")
   @PreAuthorize(PermissionConstants.ENROLLMENT_WRITE)
   public StudentEnrollmentResponse changeStatus(@PathVariable Long id, @RequestParam EnrollmentStatus status) {
      return enrollmentService.changeStatus(id, status);
   }
}