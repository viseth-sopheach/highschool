package com.seth.backend.assessment.service;

import com.seth.backend.assessment.dto.AssessmentCreateRequest;
import com.seth.backend.assessment.dto.AssessmentResponse;
import com.seth.backend.assessment.dto.AssessmentTypeResponse;
import com.seth.backend.assessment.entity.Assessment;
import com.seth.backend.assessment.entity.AssessmentType;
import com.seth.backend.assessment.mapper.AssessmentMapper;
import com.seth.backend.assessment.repository.AssessmentRepository;
import com.seth.backend.assessment.repository.AssessmentTypeRepository;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.schoolclass.repository.SchoolClassRepository;
import com.seth.backend.security.SecurityUtils;
import com.seth.backend.subject.entity.Subject;
import com.seth.backend.subject.repository.SubjectRepository;
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
public class AssessmentService {

   private final AssessmentRepository assessmentRepository;
   private final AssessmentTypeRepository assessmentTypeRepository;
   private final SchoolClassRepository schoolClassRepository;
   private final SubjectRepository subjectRepository;
   private final TeacherRepository teacherRepository;
   private final AssessmentMapper mapper;

   public List<AssessmentTypeResponse> listTypes() {
      return assessmentTypeRepository.findAll().stream()
              .map(t -> new AssessmentTypeResponse(t.getId(), t.getName(), t.getWeightDefault()))
              .toList();
   }

   public Page<AssessmentResponse> search(Long schoolClassId, Long subjectId, Pageable pageable) {
      return assessmentRepository.search(schoolClassId, subjectId, pageable).map(mapper::toResponse);
   }

   public AssessmentResponse getById(Long id) {
      return mapper.toResponse(assessmentRepository.findWithRelationsById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Assessment", id)));
   }

   @Transactional
   public AssessmentResponse create(AssessmentCreateRequest request) {
      SchoolClass schoolClass = schoolClassRepository.findById(request.schoolClassId())
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", request.schoolClassId()));
      Subject subject = subjectRepository.findById(request.subjectId())
              .orElseThrow(() -> ResourceNotFoundException.of("Subject", request.subjectId()));
      AssessmentType type = assessmentTypeRepository.findById(request.assessmentTypeId())
              .orElseThrow(() -> ResourceNotFoundException.of("AssessmentType", request.assessmentTypeId()));
      Teacher createdBy = teacherRepository.findWithUserByUserId(SecurityUtils.currentUserId())
              .orElseThrow(() -> new ResourceNotFoundException("No teacher profile linked to this account."));

      Assessment assessment = new Assessment();
      assessment.setSchoolClass(schoolClass);
      assessment.setSubject(subject);
      assessment.setAssessmentType(type);
      assessment.setTitle(request.title());
      assessment.setMaxScore(request.maxScore());
      assessment.setWeight(request.weight());
      assessment.setAssessmentDate(request.assessmentDate());
      assessment.setCreatedBy(createdBy);

      return mapper.toResponse(assessmentRepository.save(assessment));
   }
}