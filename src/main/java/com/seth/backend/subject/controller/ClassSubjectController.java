package com.seth.backend.subject.controller;

import com.seth.backend.security.PermissionConstants;
import com.seth.backend.subject.dto.ClassSubjectRequest;
import com.seth.backend.subject.dto.ClassSubjectResponse;
import com.seth.backend.subject.service.ClassSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/school-classes/{schoolClassId}/subjects")
@RequiredArgsConstructor
public class ClassSubjectController {

   private final ClassSubjectService classSubjectService;

   @GetMapping
   @PreAuthorize(PermissionConstants.CLASS_READ)
   public List<ClassSubjectResponse> list(@PathVariable Long schoolClassId) {
      return classSubjectService.listForClass(schoolClassId);
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public ClassSubjectResponse assign(@PathVariable Long schoolClassId,
                                      @Valid @RequestBody ClassSubjectRequest request) {
      return classSubjectService.assign(schoolClassId, request);
   }

   @DeleteMapping("/{classSubjectId}")
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public ResponseEntity<Void> unassign(@PathVariable Long schoolClassId, @PathVariable Long classSubjectId) {
      classSubjectService.unassign(classSubjectId);
      return ResponseEntity.noContent().build();
   }
}