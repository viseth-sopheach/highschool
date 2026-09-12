package com.seth.backend.assignment.controller;

import com.seth.backend.assignment.dto.ClassTeacherAssignmentRequest;
import com.seth.backend.assignment.dto.ClassTeacherAssignmentResponse;
import com.seth.backend.assignment.service.ClassTeacherAssignmentService;
import com.seth.backend.security.PermissionConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClassTeacherAssignmentController {

   private final ClassTeacherAssignmentService assignmentService;

   @GetMapping("/api/school-classes/{schoolClassId}/teacher-assignments")
   @PreAuthorize(PermissionConstants.CLASS_READ)
   public List<ClassTeacherAssignmentResponse> listForClass(@PathVariable Long schoolClassId) {
      return assignmentService.listForClass(schoolClassId);
   }

   @GetMapping("/api/teachers/{teacherId}/assignments")
   @PreAuthorize(PermissionConstants.CLASS_READ)
   public List<ClassTeacherAssignmentResponse> listForTeacher(@PathVariable Long teacherId) {
      return assignmentService.listForTeacher(teacherId);
   }

   @PostMapping("/api/school-classes/{schoolClassId}/teacher-assignments")
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public ClassTeacherAssignmentResponse assign(@PathVariable Long schoolClassId,
                                                @Valid @RequestBody ClassTeacherAssignmentRequest request) {
      return assignmentService.assign(schoolClassId, request);
   }

   @DeleteMapping("/api/school-classes/{schoolClassId}/teacher-assignments/{assignmentId}")
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public ResponseEntity<Void> unassign(@PathVariable Long schoolClassId, @PathVariable Long assignmentId) {
      assignmentService.unassign(assignmentId);
      return ResponseEntity.noContent().build();
   }
}