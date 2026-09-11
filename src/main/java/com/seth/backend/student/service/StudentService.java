package com.seth.backend.student.service;

import com.seth.backend.exception.AccessDeniedOnResourceException;
import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

   private final StudentRepository studentRepository;
   private final UserRepository userRepository;
   private final StudentMapper studentMapper;

   public Page<StudentResponse> list(String search, Pageable pageable) {
      String normalized = (search == null || search.isBlank()) ? null : search.trim();
      return studentRepository.search(normalized, pageable).map(studentMapper::toResponse);
   }

   /**
    * IDOR guard: a caller with STUDENT_READ_OWN (but not STUDENT_READ) may
    * only fetch the Student row linked to their own user id.
    */
   public StudentResponse getById(Long id) {
      Student student = studentRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Student", id));

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

      if (studentRepository.existsByUserId(request.userId())) {
         throw new DuplicateResourceException(
                 "User " + request.userId() + " is already linked to a student record.");
      }
      if (studentRepository.existsByStudentCode(request.studentCode())) {
         throw new DuplicateResourceException(
                 "Student code '" + request.studentCode() + "' is already in use.");
      }

      Student student = new Student();
      student.setUser(user);
      student.setStudentCode(request.studentCode());
      student.setKhmerName(request.khmerName());
      student.setEnglishName(request.englishName());
      student.setDob(request.dob());
      student.setGender(request.gender());
      student.setPhone(request.phone());

      return studentMapper.toResponse(studentRepository.save(student));
   }

   @Transactional
   public StudentResponse update(Long id, StudentUpdateRequest request) {
      Student student = studentRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Student", id));

      student.setKhmerName(request.khmerName());
      student.setEnglishName(request.englishName());
      student.setDob(request.dob());
      student.setGender(request.gender());
      student.setPhone(request.phone());

      return studentMapper.toResponse(student);
   }

   @Transactional
   public void delete(Long id) {
      if (!studentRepository.existsById(id)) {
         throw ResourceNotFoundException.of("Student", id);
      }
      studentRepository.deleteById(id);
   }
}