package com.seth.backend.subject.controller;

import com.seth.backend.security.PermissionConstants;
import com.seth.backend.subject.dto.SubjectRequest;
import com.seth.backend.subject.dto.SubjectResponse;
import com.seth.backend.subject.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

   private final SubjectService subjectService;

   @GetMapping
   public List<SubjectResponse> list() {
      return subjectService.list();
   }

   @GetMapping("/{id}")
   public SubjectResponse getById(@PathVariable Long id) {
      return subjectService.getById(id);
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest request) {
      SubjectResponse created = subjectService.create(request);
      return ResponseEntity.created(URI.create("/api/subjects/" + created.id())).body(created);
   }

   @PutMapping("/{id}")
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public SubjectResponse update(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
      return subjectService.update(id, request);
   }

   @DeleteMapping("/{id}")
   @PreAuthorize(PermissionConstants.CLASS_WRITE)
   public ResponseEntity<Void> delete(@PathVariable Long id) {
      subjectService.delete(id);
      return ResponseEntity.noContent().build();
   }
}