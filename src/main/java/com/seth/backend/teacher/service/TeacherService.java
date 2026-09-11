package com.seth.backend.teacher.service;

import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.security.SecurityUtils;
import com.seth.backend.teacher.dto.TeacherCreateRequest;
import com.seth.backend.teacher.dto.TeacherResponse;
import com.seth.backend.teacher.dto.TeacherUpdateRequest;
import com.seth.backend.teacher.entity.Teacher;
import com.seth.backend.teacher.mapper.TeacherMapper;
import com.seth.backend.teacher.repository.TeacherRepository;
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
public class TeacherService {

   private final TeacherRepository teacherRepository;
   private final UserRepository userRepository;
   private final TeacherMapper teacherMapper;

   public Page<TeacherResponse> list(String search, Pageable pageable) {
      String normalized = (search == null || search.isBlank()) ? null : search.trim();
      return teacherRepository.search(normalized, pageable).map(teacherMapper::toResponse);
   }

   public TeacherResponse getById(Long id) {
      Teacher teacher = teacherRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Teacher", id));
      return teacherMapper.toResponse(teacher);
   }

   /** Lets a logged-in teacher fetch their own profile without needing TEACHER_READ. */
   public TeacherResponse getByCurrentUser() {
      Long userId = SecurityUtils.currentUserId();
      Teacher teacher = teacherRepository.findWithUserByUserId(userId)
              .orElseThrow(() -> new ResourceNotFoundException("No teacher profile linked to this account."));
      return teacherMapper.toResponse(teacher);
   }

   @Transactional
   public TeacherResponse create(TeacherCreateRequest request) {
      User user = userRepository.findById(request.userId())
              .orElseThrow(() -> ResourceNotFoundException.of("User", request.userId()));

      if (teacherRepository.existsByUserId(request.userId())) {
         throw new DuplicateResourceException(
                 "User " + request.userId() + " is already linked to a teacher record.");
      }
      if (teacherRepository.existsByTeacherCode(request.teacherCode())) {
         throw new DuplicateResourceException(
                 "Teacher code '" + request.teacherCode() + "' is already in use.");
      }

      Teacher teacher = new Teacher();
      teacher.setUser(user);
      teacher.setTeacherCode(request.teacherCode());
      teacher.setKhmerName(request.khmerName());
      teacher.setEnglishName(request.englishName());
      teacher.setPhone(request.phone());
      teacher.setHireDate(request.hireDate());

      return teacherMapper.toResponse(teacherRepository.save(teacher));
   }

   @Transactional
   public TeacherResponse update(Long id, TeacherUpdateRequest request) {
      Teacher teacher = teacherRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Teacher", id));

      teacher.setKhmerName(request.khmerName());
      teacher.setEnglishName(request.englishName());
      teacher.setPhone(request.phone());
      teacher.setHireDate(request.hireDate());

      return teacherMapper.toResponse(teacher);
   }

   @Transactional
   public void delete(Long id) {
      if (!teacherRepository.existsById(id)) {
         throw ResourceNotFoundException.of("Teacher", id);
      }
      teacherRepository.deleteById(id);
   }
}