package com.seth.backend.assessment.controller;

import com.seth.backend.assessment.dto.ScoreBulkUpsertRequest;
import com.seth.backend.assessment.dto.ScoreResponse;
import com.seth.backend.assessment.service.ScoreService;
import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.security.PermissionConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScoreController {

   private final ScoreService scoreService;

   @GetMapping("/api/assessments/{assessmentId}/scores")
   @PreAuthorize(PermissionConstants.SCORE_READ)
   public List<ScoreResponse> listForAssessment(@PathVariable Long assessmentId) {
      return scoreService.listForAssessment(assessmentId);
   }

   @PutMapping("/api/assessments/{assessmentId}/scores")
   @PreAuthorize(PermissionConstants.SCORE_WRITE)
   public List<ScoreResponse> bulkUpsert(@PathVariable Long assessmentId,
                                         @Valid @RequestBody ScoreBulkUpsertRequest request) {
      return scoreService.bulkUpsert(assessmentId, request);
   }

   @GetMapping("/api/scores/by-enrollment/{enrollmentId}")
   @PreAuthorize(PermissionConstants.SCORE_READ_ANY_OR_OWN)
   public PageResponse<ScoreResponse> listForEnrollment(
           @PathVariable Long enrollmentId,
           @PageableDefault(size = 30) Pageable pageable) {
      scoreService.assertOwnedByCurrentUser(enrollmentId);
      return PageResponse.from(scoreService.listForEnrollment(enrollmentId, PageableUtils.capped(pageable)));
   }
}