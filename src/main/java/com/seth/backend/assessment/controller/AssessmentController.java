package com.seth.backend.assessment.controller;

import com.seth.backend.assessment.dto.AssessmentCreateRequest;
import com.seth.backend.assessment.dto.AssessmentResponse;
import com.seth.backend.assessment.dto.AssessmentTypeResponse;
import com.seth.backend.assessment.service.AssessmentService;
import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.security.PermissionConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

   private final AssessmentService assessmentService;

   @GetMapping("/types")
   @PreAuthorize(PermissionConstants.ASSESSMENT_READ)
   public List<AssessmentTypeResponse> listTypes() {
      return assessmentService.listTypes();
   }

   @GetMapping
   @PreAuthorize(PermissionConstants.ASSESSMENT_READ)
   public PageResponse<AssessmentResponse> search(
           @RequestParam(required = false) Long schoolClassId,
           @RequestParam(required = false) Long subjectId,
           @PageableDefault(size = 20, sort = "assessmentDate") Pageable pageable) {
      return PageResponse.from(assessmentService.search(schoolClassId, subjectId, PageableUtils.capped(pageable)));
   }

   @GetMapping("/{id}")
   @PreAuthorize(PermissionConstants.ASSESSMENT_READ)
   public AssessmentResponse getById(@PathVariable Long id) {
      return assessmentService.getById(id);
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.ASSESSMENT_WRITE)
   public ResponseEntity<AssessmentResponse> create(@Valid @RequestBody AssessmentCreateRequest request) {
      AssessmentResponse created = assessmentService.create(request);
      return ResponseEntity.created(URI.create("/api/assessments/" + created.id())).body(created);
   }
}