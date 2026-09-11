package com.seth.backend.student.service;

import com.seth.backend.exception.AccessDeniedOnResourceException;
import com.seth.backend.exception.BusinessRuleViolationException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.schoolclass.repository.SchoolClassRepository;
import com.seth.backend.security.SecurityUtils;
import com.seth.backend.student.dto.StudentEnrollmentRequest;
import com.seth.backend.student.dto.StudentEnrollmentResponse;
import com.seth.backend.student.entity.EnrollmentStatus;
import com.seth.backend.student.entity.Student;
import com.seth.backend.student.entity.StudentEnrollment;
import com.seth.backend.student.mapper.StudentEnrollmentMapper;
import com.seth.backend.student.repository.StudentEnrollmentRepository;
import com.seth.backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentEnrollmentService {

   private final StudentEnrollmentRepository enrollmentRepository;
   private final StudentRepository studentRepository;
   private final SchoolClassRepository schoolClassRepository;
   private final StudentEnrollmentMapper mapper;

   public Page<StudentEnrollmentResponse> listForStudent(Long studentId, Pageable pageable) {
      return enrollmentRepository.findByStudent_Id(studentId, pageable).map(mapper::toResponse);
   }

   public Page<StudentEnrollmentResponse> listForClass(Long schoolClassId, Pageable pageable) {
      return enrollmentRepository.findBySchoolClass_Id(schoolClassId, pageable).map(mapper::toResponse);
   }

   public StudentEnrollmentResponse getById(Long id) {
      return mapper.toResponse(enrollmentRepository.findWithRelationsById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("StudentEnrollment", id)));
   }

   /** Business rule: a student cannot have two ACTIVE enrollments in the same academic year. */
   @Transactional
   public StudentEnrollmentResponse enroll(StudentEnrollmentRequest request) {
      Student student = studentRepository.findById(request.studentId())
              .orElseThrow(() -> ResourceNotFoundException.of("Student", request.studentId()));
      SchoolClass schoolClass = schoolClassRepository.findWithRelationsById(request.schoolClassId())
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", request.schoolClassId()));

      boolean alreadyActive = enrollmentRepository.existsByStudent_IdAndSchoolClass_AcademicYear_IdAndStatus(
              student.getId(), schoolClass.getAcademicYear().getId(), EnrollmentStatus.ACTIVE);
      if (alreadyActive) {
         throw new BusinessRuleViolationException(
                 "Student already has an active enrollment for academic year "
                         + schoolClass.getAcademicYear().getName() + ".");
      }

      StudentEnrollment enrollment = new StudentEnrollment();
      enrollment.setStudent(student);
      enrollment.setSchoolClass(schoolClass);
      enrollment.setStatus(EnrollmentStatus.ACTIVE);

      return mapper.toResponse(enrollmentRepository.save(enrollment));
   }

   @Transactional
   public StudentEnrollmentResponse changeStatus(Long id, EnrollmentStatus status) {
      StudentEnrollment enrollment = enrollmentRepository.findWithRelationsById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("StudentEnrollment", id));
      enrollment.setStatus(status);
      return mapper.toResponse(enrollment);
   }

   /** IDOR guard for the student-facing "my enrollments" endpoint. */
   public void assertOwnedByCurrentUser(Long studentId) {
      Student student = studentRepository.findWithUserById(studentId)
              .orElseThrow(() -> ResourceNotFoundException.of("Student", studentId));
      if (!student.getUser().getId().equals(SecurityUtils.currentUserId())) {
         throw new AccessDeniedOnResourceException("Not authorized to view this student's enrollments.");
      }
   }
}