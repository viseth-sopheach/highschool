package com.seth.backend.academic.controller;

import com.seth.backend.academic.dto.AcademicYearResponse;
import com.seth.backend.academic.dto.GradeResponse;
import com.seth.backend.academic.dto.StudyTrackResponse;
import com.seth.backend.academic.entity.AcademicYear;
import com.seth.backend.academic.repository.AcademicYearRepository;
import com.seth.backend.academic.repository.GradeRepository;
import com.seth.backend.academic.repository.StudyTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Reference/lookup data (year names, grade levels, track names) needed to
 * build class-management and enrollment forms. Deliberately open to any
 * authenticated user with no permission check — it's small, non-sensitive
 * catalog data, not student/teacher records.
 */
@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
public class AcademicLookupController {

   private final AcademicYearRepository academicYearRepository;
   private final GradeRepository gradeRepository;
   private final StudyTrackRepository studyTrackRepository;

   @GetMapping("/years")
   public List<AcademicYearResponse> years() {
      return academicYearRepository.findAllByOrderByStartDateDesc().stream()
              .map(this::toResponse)
              .toList();
   }

   @GetMapping("/grades")
   public List<GradeResponse> grades() {
      return gradeRepository.findAllByOrderByLevelAsc().stream()
              .map(g -> new GradeResponse(g.getId(), g.getName(), g.getLevel(), g.isRequiresTrack()))
              .toList();
   }

   @GetMapping("/study-tracks")
   public List<StudyTrackResponse> studyTracks() {
      return studyTrackRepository.findAll().stream()
              .map(t -> new StudyTrackResponse(t.getId(), t.getCode(), t.getNameKm(), t.getNameEn()))
              .toList();
   }

   private AcademicYearResponse toResponse(AcademicYear y) {
      return new AcademicYearResponse(y.getId(), y.getName(), y.getStartDate(), y.getEndDate(), y.isCurrent());
   }
}