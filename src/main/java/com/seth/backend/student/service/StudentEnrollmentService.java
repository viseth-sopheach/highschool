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
import org.springframework.dao.DataIntegrityViolationException;
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

   /**
    * Business rule: a student cannot have two ACTIVE enrollments in the same
    * academic year. Checked here for a clean 422, and backed by
    * uq_one_active_enrollment_per_year (V13) at the DB level in case two
    * concurrent requests both pass this check.
    */
   @Transactional
   public StudentEnrollmentResponse enroll(StudentEnrollmentRequest request) {
      Student student = studentRepository.findById(request.studentId())
              .orElseThrow(() -> ResourceNotFoundException.of("Student", request.studentId()));
      SchoolClass schoolClass = schoolClassRepository.findWithRelationsById(request.schoolClassId())
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", request.schoolClassId()));

      boolean alreadyActive = enrollmentRepository.existsByStudent_IdAndAcademicYear_IdAndStatus(
              student.getId(), schoolClass.getAcademicYear().getId(), EnrollmentStatus.ACTIVE);
      if (alreadyActive) {
         throw new BusinessRuleViolationException(
                 "Student already has an active enrollment for academic year "
                         + schoolClass.getAcademicYear().getName() + ".");
      }

      StudentEnrollment enrollment = new StudentEnrollment();
      enrollment.setStudent(student);
      enrollment.setSchoolClass(schoolClass);
      enrollment.setAcademicYear(schoolClass.getAcademicYear());
      enrollment.setStatus(EnrollmentStatus.ACTIVE);

      try {
         return mapper.toResponse(enrollmentRepository.save(enrollment));
      } catch (DataIntegrityViolationException ex) {
         // Race lost to a concurrent request that also passed the check above.
         throw new BusinessRuleViolationException(
                 "Student already has an active enrollment for academic year "
                         + schoolClass.getAcademicYear().getName() + ".");
      }
   }

   @Transactional
   public StudentEnrollmentResponse changeStatus(Long id, EnrollmentStatus status) {
      StudentEnrollment enrollment = enrollmentRepository.findWithRelationsById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("StudentEnrollment", id));
      enrollment.setStatus(status);
      return mapper.toResponse(enrollment);
   }

   /**
    * Designates the class president for this enrollment's class/year.
    * Unsets any existing president for the same class first — a class has
    * at most one, enforced by uq_class_president_per_class (V13).
    */
   @Transactional
   public StudentEnrollmentResponse setClassPresident(Long enrollmentId) {
      StudentEnrollment target = enrollmentRepository.findWithRelationsById(enrollmentId)
              .orElseThrow(() -> ResourceNotFoundException.of("StudentEnrollment", enrollmentId));

      enrollmentRepository.findBySchoolClass_Id(target.getSchoolClass().getId(), Pageable.unpaged())
              .stream()
              .filter(StudentEnrollment::isClassPresident)
              .forEach(e -> e.setClassPresident(false));

      target.setClassPresident(true);
      return mapper.toResponse(target);
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