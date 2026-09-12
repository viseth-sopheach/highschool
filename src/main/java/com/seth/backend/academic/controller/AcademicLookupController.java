package com.seth.backend.academic.controller;

import com.seth.backend.academic.dto.AcademicYearResponse;
import com.seth.backend.academic.dto.GradeResponse;
import com.seth.backend.academic.dto.GradingScaleResponse;
import com.seth.backend.academic.dto.StudyTrackResponse;
import com.seth.backend.academic.service.AcademicLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
public class AcademicLookupController {

   private final AcademicLookupService lookupService;

   @GetMapping("/years")
   public List<AcademicYearResponse> years() { return lookupService.years(); }

   @GetMapping("/grades")
   public List<GradeResponse> grades() { return lookupService.grades(); }

   @GetMapping("/study-tracks")
   public List<StudyTrackResponse> studyTracks() { return lookupService.studyTracks(); }

   @GetMapping("/grading-scales")
   public List<GradingScaleResponse> gradingScales() { return lookupService.gradingScales(); }
}