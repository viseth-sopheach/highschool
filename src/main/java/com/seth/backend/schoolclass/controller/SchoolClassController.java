package com.seth.backend.schoolclass.controller;

import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.schoolclass.dto.SchoolClassCreateRequest;
import com.seth.backend.schoolclass.dto.SchoolClassResponse;
import com.seth.backend.schoolclass.dto.SchoolClassUpdateRequest;
import com.seth.backend.schoolclass.service.SchoolClassService;
import com.seth.backend.security.PermissionConstants;
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
@RequestMapping("/api/school-classes")
@RequiredArgsConstructor
public class SchoolClassController {

   private final SchoolClassService schoolClassService;

   @GetMapping
   @PreAuthorize(PermissionConstants.CLASS_READ)
   public PageResponse<SchoolClassResponse> list(
           @RequestParam(required = false) Long academicYearId,
           @RequestParam(required = false) Long gradeId,
           @RequestParam(required = false) String search,
           @PageableDefault(size = 20, sort = "name") Pageable pageable) {
      return PageResponse.from(
              schoolClassService.list(academicYearId, gradeId, search, PageableUtils.capped(pageable)));
   }

   @GetMapping("/{id}")
   @PreAuthorize(PermissionConstants.CLASS_READ)
   public SchoolClassResponse getById(@PathVariable Long id) {
      return schoolClassService.getById(id);
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public ResponseEntity<SchoolClassResponse> create(@Valid @RequestBody SchoolClassCreateRequest request) {
      SchoolClassResponse created = schoolClassService.create(request);
      return ResponseEntity.created(URI.create("/api/school-classes/" + created.id())).body(created);
   }

   @PutMapping("/{id}")
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public SchoolClassResponse update(@PathVariable Long id, @Valid @RequestBody SchoolClassUpdateRequest request) {
      return schoolClassService.update(id, request);
   }

   @DeleteMapping("/{id}")
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public ResponseEntity<Void> delete(@PathVariable Long id) {
      schoolClassService.delete(id);
      return ResponseEntity.noContent().build();
   }
}