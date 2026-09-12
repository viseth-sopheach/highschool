package com.seth.backend.academic.service;

import com.seth.backend.academic.dto.AcademicYearResponse;
import com.seth.backend.academic.dto.GradeResponse;
import com.seth.backend.academic.dto.GradingScaleResponse;
import com.seth.backend.academic.dto.StudyTrackResponse;
import com.seth.backend.academic.entity.AcademicYear;
import com.seth.backend.academic.entity.GradingScale;
import com.seth.backend.academic.repository.AcademicYearRepository;
import com.seth.backend.academic.repository.GradeRepository;
import com.seth.backend.academic.repository.GradingScaleRepository;
import com.seth.backend.academic.repository.StudyTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Small, rarely-changing catalog tables (years/grades/tracks/grading scale)
 * are read on nearly every form-loading request across the app. They're
 * cached in-process via Caffeine (see application.yml) rather than hit the
 * DB every time — safe because writes to these tables are rare/admin-only
 * and there is currently no update path wired up (add @CacheEvict on the
 * relevant cache name the day a write endpoint is added for these tables).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicLookupService {

   private final AcademicYearRepository academicYearRepository;
   private final GradeRepository gradeRepository;
   private final StudyTrackRepository studyTrackRepository;
   private final GradingScaleRepository gradingScaleRepository;

   @Cacheable("academicYears")
   public List<AcademicYearResponse> years() {
      return academicYearRepository.findAllByOrderByStartDateDesc().stream()
              .map(this::toResponse).toList();
   }

   @Cacheable("grades")
   public List<GradeResponse> grades() {
      return gradeRepository.findAllByOrderByLevelAsc().stream()
              .map(g -> new GradeResponse(g.getId(), g.getName(), g.getLevel(), g.isRequiresTrack()))
              .toList();
   }

   @Cacheable("studyTracks")
   public List<StudyTrackResponse> studyTracks() {
      return studyTrackRepository.findAll().stream()
              .map(t -> new StudyTrackResponse(t.getId(), t.getCode(), t.getNameKm(), t.getNameEn()))
              .toList();
   }

   @Cacheable("gradingScales")
   public List<GradingScaleResponse> gradingScales() {
      return gradingScaleRepository.findAllByOrderByMinScoreDesc().stream()
              .map(s -> new GradingScaleResponse(s.getId(), s.getMinScore(), s.getMaxScore(),
                      s.getLetterGrade(), s.getGpaPoint(), s.getDescription()))
              .toList();
   }

   /** Used by ScoreService to attach a letter grade without re-querying per score. */
   public String resolveLetterGrade(double score) {
      return gradingScales().stream()
              .filter(g -> score >= g.minScore().doubleValue() && score <= g.maxScore().doubleValue())
              .map(GradingScaleResponse::letterGrade)
              .findFirst()
              .orElse(null);
   }

   private AcademicYearResponse toResponse(AcademicYear y) {
      return new AcademicYearResponse(y.getId(), y.getName(), y.getStartDate(), y.getEndDate(), y.isCurrent());
   }
}