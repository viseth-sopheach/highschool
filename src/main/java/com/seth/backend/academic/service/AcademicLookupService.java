package com.seth.backend.academic.service;

import com.seth.backend.academic.dto.AcademicYearResponse;
import com.seth.backend.academic.dto.GradeResponse;
import com.seth.backend.academic.dto.GradingScaleResponse;
import com.seth.backend.academic.dto.StudyTrackResponse;
import com.seth.backend.academic.entity.AcademicYear;
import com.seth.backend.academic.repository.AcademicYearRepository;
import com.seth.backend.academic.repository.GradeRepository;
import com.seth.backend.academic.repository.GradingScaleRepository;
import com.seth.backend.academic.repository.StudyTrackRepository;
import com.seth.backend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicLookupService {

   private final AcademicYearRepository academicYearRepository;
   private final GradeRepository gradeRepository;
   private final StudyTrackRepository studyTrackRepository;
   private final GradingScaleRepository gradingScaleRepository;

   @Cacheable(value = "academicYears", key = "T(com.seth.backend.security.SecurityUtils).currentSchoolId()")
   public List<AcademicYearResponse> years() {
      Long schoolId = SecurityUtils.currentSchoolId();
      return academicYearRepository.findBySchool_IdOrderByStartDateDesc(schoolId).stream()
              .map(this::toResponse).toList();
   }

   @Cacheable(value = "grades", key = "T(com.seth.backend.security.SecurityUtils).currentSchoolId()")
   public List<GradeResponse> grades() {
      Long schoolId = SecurityUtils.currentSchoolId();
      return gradeRepository.findBySchool_IdOrderByLevelAsc(schoolId).stream()
              .map(g -> new GradeResponse(g.getId(), g.getName(), g.getLevel(), g.isRequiresTrack()))
              .toList();
   }

   @Cacheable(value = "studyTracks", key = "T(com.seth.backend.security.SecurityUtils).currentSchoolId()")
   public List<StudyTrackResponse> studyTracks() {
      Long schoolId = SecurityUtils.currentSchoolId();
      return studyTrackRepository.findBySchool_Id(schoolId).stream()
              .map(t -> new StudyTrackResponse(t.getId(), t.getCode(), t.getNameKm(), t.getNameEn()))
              .toList();
   }

   @Cacheable(value = "gradingScales", key = "T(com.seth.backend.security.SecurityUtils).currentSchoolId()")
   public List<GradingScaleResponse> gradingScales() {
      Long schoolId = SecurityUtils.currentSchoolId();
      return gradingScaleRepository.findBySchool_IdOrderByMinScoreDesc(schoolId).stream()
              .map(s -> new GradingScaleResponse(s.getId(), s.getMinScore(), s.getMaxScore(),
                      s.getLetterGrade(), s.getGpaPoint(), s.getDescription()))
              .toList();
   }

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