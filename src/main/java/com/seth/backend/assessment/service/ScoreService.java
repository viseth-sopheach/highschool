package com.seth.backend.assessment.service;

import com.seth.backend.academic.service.AcademicLookupService;
import com.seth.backend.assessment.dto.ScoreBulkUpsertRequest;
import com.seth.backend.assessment.dto.ScoreEntryRequest;
import com.seth.backend.assessment.dto.ScoreResponse;
import com.seth.backend.assessment.entity.Assessment;
import com.seth.backend.assessment.entity.Score;
import com.seth.backend.assessment.mapper.ScoreMapper;
import com.seth.backend.assessment.repository.AssessmentRepository;
import com.seth.backend.assessment.repository.ScoreRepository;
import com.seth.backend.exception.AccessDeniedOnResourceException;
import com.seth.backend.exception.BusinessRuleViolationException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.security.SecurityUtils;
import com.seth.backend.student.entity.StudentEnrollment;
import com.seth.backend.student.repository.StudentEnrollmentRepository;
import com.seth.backend.teacher.entity.Teacher;
import com.seth.backend.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreService {

   private final ScoreRepository scoreRepository;
   private final AssessmentRepository assessmentRepository;
   private final StudentEnrollmentRepository enrollmentRepository;
   private final TeacherRepository teacherRepository;
   private final ScoreMapper mapper;
   private final AcademicLookupService academicLookupService;

   public List<ScoreResponse> listForAssessment(Long assessmentId) {
      return scoreRepository.findByAssessment_Id(assessmentId).stream()
              .map(this::withLetterGrade).toList();
   }

   public Page<ScoreResponse> listForEnrollment(Long enrollmentId, Pageable pageable) {
      return scoreRepository.findByStudentEnrollment_IdOrderByCreatedAtDesc(enrollmentId, pageable)
              .map(this::withLetterGrade);
   }

   /**
    * Grades an entire class roster for one assessment in a single transaction
    * (upsert per enrollment). Mirrors the trg_validate_score_max DB trigger
    * in the application layer first so the client gets a clean 422 instead of
    * a 409 from the trigger firing mid-batch.
    */
   @Transactional
   public List<ScoreResponse> bulkUpsert(Long assessmentId, ScoreBulkUpsertRequest request) {
      Assessment assessment = assessmentRepository.findWithRelationsById(assessmentId)
              .orElseThrow(() -> ResourceNotFoundException.of("Assessment", assessmentId));
      Teacher recordedBy = teacherRepository.findWithUserByUserId(SecurityUtils.currentUserId())
              .orElseThrow(() -> new ResourceNotFoundException("No teacher profile linked to this account."));

      return request.entries().stream()
              .map(entry -> upsertOne(assessment, recordedBy, entry))
              .map(this::withLetterGrade)
              .toList();
   }

   private Score upsertOne(Assessment assessment, Teacher recordedBy, ScoreEntryRequest entry) {
      if (entry.score().compareTo(assessment.getMaxScore()) > 0) {
         throw new BusinessRuleViolationException(
                 "Score " + entry.score() + " exceeds assessment max_score " + assessment.getMaxScore());
      }

      StudentEnrollment enrollment = enrollmentRepository.findById(entry.studentEnrollmentId())
              .orElseThrow(() -> ResourceNotFoundException.of("StudentEnrollment", entry.studentEnrollmentId()));

      Score score = scoreRepository.findByAssessment_IdAndStudentEnrollment_Id(
              assessment.getId(), enrollment.getId()).orElseGet(Score::new);

      score.setAssessment(assessment);
      score.setStudentEnrollment(enrollment);
      score.setScore(entry.score());
      score.setRemarks(entry.remarks());
      score.setRecordedBy(recordedBy);

      return scoreRepository.save(score);
   }

   /** IDOR guard: a student may only view scores for their own enrollment. */
   public void assertOwnedByCurrentUser(Long enrollmentId) {
      StudentEnrollment enrollment = enrollmentRepository.findWithRelationsById(enrollmentId)
              .orElseThrow(() -> ResourceNotFoundException.of("StudentEnrollment", enrollmentId));
      if (!enrollment.getStudent().getUser().getId().equals(SecurityUtils.currentUserId())) {
         throw new AccessDeniedOnResourceException("Not authorized to view these scores.");
      }
   }

   private ScoreResponse withLetterGrade(Score score) {
      ScoreResponse base = mapper.toResponse(score);
      String letter = academicLookupService.resolveLetterGrade(score.getScore().doubleValue());
      return new ScoreResponse(base.id(), base.assessmentId(), base.assessmentTitle(),
              base.studentEnrollmentId(), base.studentCode(), base.score(), base.maxScore(),
              letter, base.remarks());
   }
}