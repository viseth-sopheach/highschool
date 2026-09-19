package com.seth.backend.student.service;

import com.seth.backend.audit.AuditActions;
import com.seth.backend.audit.service.AuditLogService;
import com.seth.backend.exception.AccessDeniedOnResourceException;
import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.school.repository.SchoolRepository;
import com.seth.backend.security.SecurityUtils;
import com.seth.backend.student.dto.StudentCreateRequest;
import com.seth.backend.student.dto.StudentResponse;
import com.seth.backend.student.dto.StudentUpdateRequest;
import com.seth.backend.student.entity.Student;
import com.seth.backend.student.mapper.StudentMapper;
import com.seth.backend.student.repository.StudentRepository;
import com.seth.backend.user.entity.User;
import com.seth.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

   private final StudentRepository studentRepository;
   private final UserRepository userRepository;
   private final SchoolRepository schoolRepository;
   private final StudentMapper studentMapper;
   private final AuditLogService auditLogService;

   public Page<StudentResponse> list(String search, Pageable pageable) {
      String normalized = (search == null || search.isBlank()) ? "" : search.trim();
      return studentRepository.search(SecurityUtils.currentSchoolId(), normalized, pageable)
              .map(studentMapper::toResponse);
   }

   public StudentResponse getById(Long id) {
      Student student = studentRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Student", id));

      boolean sameSchool = student.getSchool().getId().equals(SecurityUtils.currentSchoolId());
      if (!sameSchool) {
         throw new AccessDeniedOnResourceException("This student does not belong to your school.");
      }
      if (!SecurityUtils.hasAuthority("PERM_STUDENT_READ")
              && !student.getUser().getId().equals(SecurityUtils.currentUserId())) {
         throw new AccessDeniedOnResourceException("Not authorized to view this student.");
      }
      return studentMapper.toResponse(student);
   }

   public StudentResponse getByCurrentUser() {
      Long userId = SecurityUtils.currentUserId();
      Student student = studentRepository.findWithUserByUserId(userId)
              .orElseThrow(() -> new ResourceNotFoundException("No student profile linked to this account."));
      return studentMapper.toResponse(student);
   }

   @Transactional
   public StudentResponse create(StudentCreateRequest request) {
      User user = userRepository.findById(request.userId())
              .orElseThrow(() -> ResourceNotFoundException.of("User", request.userId()));

      Long schoolId = SecurityUtils.currentSchoolId();
      if (!user.getSchool().getId().equals(schoolId)) {
         throw new AccessDeniedOnResourceException("User does not belong to your school.");
      }
      if (studentRepository.existsByUserId(request.userId())) {
         throw new DuplicateResourceException(
                 "User " + request.userId() + " is already linked to a student record.");
      }
      if (studentRepository.existsBySchool_IdAndStudentCode(schoolId, request.studentCode())) {
         throw new DuplicateResourceException(
                 "Student code '" + request.studentCode() + "' is already in use.");
      }

      Student student = new Student();
      student.setSchool(schoolRepository.getReferenceById(schoolId));
      student.setUser(user);
      student.setStudentCode(request.studentCode());
      student.setKhmerName(request.khmerName());
      student.setEnglishName(request.englishName());
      student.setDob(request.dob());
      student.setGender(request.gender());
      student.setPhone(request.phone());

      Student saved = studentRepository.save(student);
      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.STUDENT_CREATED, "Student", saved.getId(),
              Map.of("studentCode", saved.getStudentCode()));
      return studentMapper.toResponse(saved);
   }

   @Transactional
   public StudentResponse update(Long id, StudentUpdateRequest request) {
      Student student = studentRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Student", id));
      assertSameSchool(student);

      student.setKhmerName(request.khmerName());
      student.setEnglishName(request.englishName());
      student.setDob(request.dob());
      student.setGender(request.gender());
      student.setPhone(request.phone());

      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.STUDENT_UPDATED, "Student", id);
      return studentMapper.toResponse(student);
   }

   @Transactional
   public void delete(Long id) {
      Student student = studentRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Student", id));
      assertSameSchool(student);
      studentRepository.deleteById(id);
      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.STUDENT_DELETED, "Student", id);
   }

   private void assertSameSchool(Student student) {
      if (!student.getSchool().getId().equals(SecurityUtils.currentSchoolId())) {
         throw new AccessDeniedOnResourceException("This student does not belong to your school.");
      }
   }
}