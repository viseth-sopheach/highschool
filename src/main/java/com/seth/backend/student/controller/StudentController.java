package com.seth.backend.student.controller;

import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.security.PermissionConstants;
import com.seth.backend.student.dto.StudentCreateRequest;
import com.seth.backend.student.dto.StudentResponse;
import com.seth.backend.student.dto.StudentUpdateRequest;
import com.seth.backend.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

   private final StudentService studentService;

   @GetMapping
   @PreAuthorize(PermissionConstants.STUDENT_READ)
   public PageResponse<StudentResponse> list(
           @RequestParam(required = false) String search,
           @PageableDefault(size = 20, sort = "khmerName") Pageable pageable) {
      return PageResponse.from(studentService.list(search, PageableUtils.capped(pageable)));
   }

   /** IDOR-safe: service layer enforces STUDENT_READ or self-ownership. */
   @GetMapping("/{id}")
   @PreAuthorize(PermissionConstants.STUDENT_READ_ANY_OR_OWN)
   public StudentResponse getById(@PathVariable Long id) {
      return studentService.getById(id);
   }

   @GetMapping("/me")
   public StudentResponse getMyProfile() {
      return studentService.getByCurrentUser();
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.STUDENT_WRITE)
   public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentCreateRequest request) {
      StudentResponse created = studentService.create(request);
      return ResponseEntity.created(URI.create("/api/students/" + created.id())).body(created);
   }

   @PutMapping("/{id}")
   @PreAuthorize(PermissionConstants.STUDENT_WRITE)
   public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentUpdateRequest request) {
      return studentService.update(id, request);
   }

   @DeleteMapping("/{id}")
   @PreAuthorize(PermissionConstants.STUDENT_WRITE)
   public ResponseEntity<Void> delete(@PathVariable Long id) {
      studentService.delete(id);
      return ResponseEntity.noContent().build();
   }
}