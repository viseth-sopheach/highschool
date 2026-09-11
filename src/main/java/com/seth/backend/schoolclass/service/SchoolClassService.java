package com.seth.backend.schoolclass.service;

import com.seth.backend.academic.entity.AcademicYear;
import com.seth.backend.academic.entity.Grade;
import com.seth.backend.academic.entity.StudyTrack;
import com.seth.backend.academic.repository.AcademicYearRepository;
import com.seth.backend.academic.repository.GradeRepository;
import com.seth.backend.academic.repository.StudyTrackRepository;
import com.seth.backend.exception.BusinessRuleViolationException;
import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.schoolclass.dto.SchoolClassCreateRequest;
import com.seth.backend.schoolclass.dto.SchoolClassResponse;
import com.seth.backend.schoolclass.dto.SchoolClassUpdateRequest;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.schoolclass.mapper.SchoolClassMapper;
import com.seth.backend.schoolclass.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolClassService {

   private final SchoolClassRepository schoolClassRepository;
   private final AcademicYearRepository academicYearRepository;
   private final GradeRepository gradeRepository;
   private final StudyTrackRepository studyTrackRepository;
   private final SchoolClassMapper mapper;

   public Page<SchoolClassResponse> list(Long academicYearId, Long gradeId, String search, Pageable pageable) {
      String normalized = (search == null || search.isBlank()) ? null : search.trim();
      return schoolClassRepository.search(academicYearId, gradeId, normalized, pageable)
              .map(mapper::toResponse);
   }

   public SchoolClassResponse getById(Long id) {
      SchoolClass sc = schoolClassRepository.findWithRelationsById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", id));
      return mapper.toResponse(sc);
   }

   @Transactional
   public SchoolClassResponse create(SchoolClassCreateRequest request) {
      AcademicYear year = academicYearRepository.findById(request.academicYearId())
              .orElseThrow(() -> ResourceNotFoundException.of("AcademicYear", request.academicYearId()));
      Grade grade = gradeRepository.findById(request.gradeId())
              .orElseThrow(() -> ResourceNotFoundException.of("Grade", request.gradeId()));

      StudyTrack track = validateTrack(grade, request.studyTrackId());
      assertNotDuplicate(year.getId(), grade.getId(), track, request.name());

      SchoolClass sc = new SchoolClass();
      sc.setAcademicYear(year);
      sc.setGrade(grade);
      sc.setStudyTrack(track);
      sc.setName(request.name());
      sc.setCapacity(request.capacity());

      return mapper.toResponse(schoolClassRepository.save(sc));
   }

   @Transactional
   public SchoolClassResponse update(Long id, SchoolClassUpdateRequest request) {
      SchoolClass sc = schoolClassRepository.findWithRelationsById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", id));

      StudyTrack track = validateTrack(sc.getGrade(), request.studyTrackId());
      if (!sc.getName().equalsIgnoreCase(request.name())
              || !sameTrack(sc.getStudyTrack(), track)) {
         assertNotDuplicate(sc.getAcademicYear().getId(), sc.getGrade().getId(), track, request.name());
      }

      sc.setName(request.name());
      sc.setStudyTrack(track);
      sc.setCapacity(request.capacity());

      return mapper.toResponse(sc);
   }

   @Transactional
   public void delete(Long id) {
      if (!schoolClassRepository.existsById(id)) {
         throw ResourceNotFoundException.of("SchoolClass", id);
      }
      schoolClassRepository.deleteById(id);
   }

   /**
    * Mirrors the DB trigger {@code validate_school_class_track}: grades with
    * requires_track = true must get a track, grades without it must not.
    * Checked here too so the client gets a clean 422 instead of a raw
    * trigger exception surfacing as an opaque 500/409.
    */
   private StudyTrack validateTrack(Grade grade, Long studyTrackId) {
      if (grade.isRequiresTrack()) {
         if (studyTrackId == null) {
            throw new BusinessRuleViolationException(
                    "Grade '" + grade.getName() + "' requires a study track.");
         }
         return studyTrackRepository.findById(studyTrackId)
                 .orElseThrow(() -> ResourceNotFoundException.of("StudyTrack", studyTrackId));
      }
      if (studyTrackId != null) {
         throw new BusinessRuleViolationException(
                 "Grade '" + grade.getName() + "' must not have a study track.");
      }
      return null;
   }

   private void assertNotDuplicate(Long academicYearId, Long gradeId, StudyTrack track, String name) {
      boolean duplicate = track == null
              ? schoolClassRepository.existsByAcademicYearIdAndGradeIdAndStudyTrackIdIsNullAndName(
              academicYearId, gradeId, name)
              : schoolClassRepository.existsByAcademicYearIdAndGradeIdAndStudyTrackIdAndName(
              academicYearId, gradeId, track.getId(), name);
      if (duplicate) {
         throw new DuplicateResourceException(
                 "A class named '" + name + "' already exists for this year/grade/track.");
      }
   }

   private boolean sameTrack(StudyTrack a, StudyTrack b) {
      if (a == null || b == null) {
         return a == b;
      }
      return a.getId().equals(b.getId());
   }
}