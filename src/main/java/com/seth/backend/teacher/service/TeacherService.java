package com.seth.backend.teacher.service;

import com.seth.backend.audit.AuditActions;
import com.seth.backend.audit.service.AuditLogService;
import com.seth.backend.exception.AccessDeniedOnResourceException;
import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.school.repository.SchoolRepository;
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

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherService {

   private final TeacherRepository teacherRepository;
   private final UserRepository userRepository;
   private final SchoolRepository schoolRepository;
   private final TeacherMapper teacherMapper;
   private final AuditLogService auditLogService;

   public Page<TeacherResponse> list(String search, Pageable pageable) {
      String normalized = (search == null || search.isBlank()) ? "" : search.trim();
      return teacherRepository.search(SecurityUtils.currentSchoolId(), normalized, pageable)
              .map(teacherMapper::toResponse);
   }

   public TeacherResponse getById(Long id) {
      Teacher teacher = teacherRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Teacher", id));
      assertSameSchool(teacher);
      return teacherMapper.toResponse(teacher);
   }

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

      Long schoolId = SecurityUtils.currentSchoolId();
      if (!user.getSchool().getId().equals(schoolId)) {
         throw new AccessDeniedOnResourceException("User does not belong to your school.");
      }
      if (teacherRepository.existsByUserId(request.userId())) {
         throw new DuplicateResourceException(
                 "User " + request.userId() + " is already linked to a teacher record.");
      }
      if (teacherRepository.existsBySchool_IdAndTeacherCode(schoolId, request.teacherCode())) {
         throw new DuplicateResourceException(
                 "Teacher code '" + request.teacherCode() + "' is already in use.");
      }

      Teacher teacher = new Teacher();
      teacher.setSchool(schoolRepository.getReferenceById(schoolId));
      teacher.setUser(user);
      teacher.setTeacherCode(request.teacherCode());
      teacher.setKhmerName(request.khmerName());
      teacher.setEnglishName(request.englishName());
      teacher.setPhone(request.phone());
      teacher.setHireDate(request.hireDate());

      Teacher saved = teacherRepository.save(teacher);
      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.TEACHER_CREATED, "Teacher", saved.getId(),
              Map.of("teacherCode", saved.getTeacherCode()));
      return teacherMapper.toResponse(saved);
   }

   @Transactional
   public TeacherResponse update(Long id, TeacherUpdateRequest request) {
      Teacher teacher = teacherRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Teacher", id));
      assertSameSchool(teacher);

      teacher.setKhmerName(request.khmerName());
      teacher.setEnglishName(request.englishName());
      teacher.setPhone(request.phone());
      teacher.setHireDate(request.hireDate());

      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.TEACHER_UPDATED, "Teacher", id);
      return teacherMapper.toResponse(teacher);
   }

   @Transactional
   public void delete(Long id) {
      Teacher teacher = teacherRepository.findWithUserById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Teacher", id));
      assertSameSchool(teacher);
      teacherRepository.deleteById(id);
      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.TEACHER_DELETED, "Teacher", id);
   }

   private void assertSameSchool(Teacher teacher) {
      if (!teacher.getSchool().getId().equals(SecurityUtils.currentSchoolId())) {
         throw new AccessDeniedOnResourceException("This teacher does not belong to your school.");
      }
   }
}