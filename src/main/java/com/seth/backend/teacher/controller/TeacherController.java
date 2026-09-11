package com.seth.backend.teacher.controller;

import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.security.PermissionConstants;
import com.seth.backend.teacher.dto.TeacherCreateRequest;
import com.seth.backend.teacher.dto.TeacherResponse;
import com.seth.backend.teacher.dto.TeacherUpdateRequest;
import com.seth.backend.teacher.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

   private final TeacherService teacherService;

   @GetMapping
   @PreAuthorize(PermissionConstants.TEACHER_READ)
   public PageResponse<TeacherResponse> list(
           @RequestParam(required = false) String search,
           @PageableDefault(size = 20, sort = "khmerName") Pageable pageable) {
      return PageResponse.from(teacherService.list(search, PageableUtils.capped(pageable)));
   }

   @GetMapping("/{id}")
   @PreAuthorize(PermissionConstants.TEACHER_READ)
   public TeacherResponse getById(@PathVariable Long id) {
      return teacherService.getById(id);
   }

   /** No @PreAuthorize on purpose — any authenticated user may read their own linked teacher profile. */
   @GetMapping("/me")
   public TeacherResponse getMyProfile() {
      return teacherService.getByCurrentUser();
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.TEACHER_WRITE)
   public ResponseEntity<TeacherResponse> create(@Valid @RequestBody TeacherCreateRequest request) {
      TeacherResponse created = teacherService.create(request);
      return ResponseEntity.created(URI.create("/api/teachers/" + created.id())).body(created);
   }

   @PutMapping("/{id}")
   @PreAuthorize(PermissionConstants.TEACHER_WRITE)
   public TeacherResponse update(@PathVariable Long id, @Valid @RequestBody TeacherUpdateRequest request) {
      return teacherService.update(id, request);
   }

   @DeleteMapping("/{id}")
   @PreAuthorize(PermissionConstants.TEACHER_WRITE)
   public ResponseEntity<Void> delete(@PathVariable Long id) {
      teacherService.delete(id);
      return ResponseEntity.noContent().build();
   }
}