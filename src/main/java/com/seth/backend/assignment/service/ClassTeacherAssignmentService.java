package com.seth.backend.assignment.service;

import com.seth.backend.assignment.dto.ClassTeacherAssignmentRequest;
import com.seth.backend.assignment.dto.ClassTeacherAssignmentResponse;
import com.seth.backend.assignment.entity.ClassTeacherAssignment;
import com.seth.backend.assignment.mapper.ClassTeacherAssignmentMapper;
import com.seth.backend.assignment.repository.ClassTeacherAssignmentRepository;
import com.seth.backend.exception.BusinessRuleViolationException;
import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.schoolclass.repository.SchoolClassRepository;
import com.seth.backend.subject.entity.Subject;
import com.seth.backend.subject.repository.SubjectRepository;
import com.seth.backend.teacher.entity.Teacher;
import com.seth.backend.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassTeacherAssignmentService {

   private final ClassTeacherAssignmentRepository assignmentRepository;
   private final SchoolClassRepository schoolClassRepository;
   private final TeacherRepository teacherRepository;
   private final SubjectRepository subjectRepository;
   private final ClassTeacherAssignmentMapper mapper;

   public List<ClassTeacherAssignmentResponse> listForClass(Long schoolClassId) {
      return assignmentRepository.findBySchoolClass_Id(schoolClassId).stream()
              .map(mapper::toResponse).toList();
   }

   public List<ClassTeacherAssignmentResponse> listForTeacher(Long teacherId) {
      return assignmentRepository.findByTeacher_Id(teacherId).stream()
              .map(mapper::toResponse).toList();
   }

   @Transactional
   public ClassTeacherAssignmentResponse assign(Long schoolClassId, ClassTeacherAssignmentRequest request) {
      SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", schoolClassId));
      Teacher teacher = teacherRepository.findById(request.teacherId())
              .orElseThrow(() -> ResourceNotFoundException.of("Teacher", request.teacherId()));

      Subject subject = null;
      if (request.subjectId() != null) {
         subject = subjectRepository.findById(request.subjectId())
                 .orElseThrow(() -> ResourceNotFoundException.of("Subject", request.subjectId()));
      }

      if (request.homeroom() && assignmentRepository.existsBySchoolClass_IdAndHomeroomTrue(schoolClassId)) {
         throw new BusinessRuleViolationException("This class already has a homeroom teacher assigned.");
      }

      boolean duplicate = subject == null
              ? assignmentRepository.existsBySchoolClass_IdAndTeacher_IdAndSubjectIsNull(schoolClassId, teacher.getId())
              : assignmentRepository.existsBySchoolClass_IdAndTeacher_IdAndSubject_Id(schoolClassId, teacher.getId(), subject.getId());
      if (duplicate) {
         throw new DuplicateResourceException("This teacher is already assigned to this class/subject.");
      }

      ClassTeacherAssignment assignment = new ClassTeacherAssignment();
      assignment.setSchoolClass(schoolClass);
      assignment.setTeacher(teacher);
      assignment.setSubject(subject);
      assignment.setHomeroom(request.homeroom());

      return mapper.toResponse(assignmentRepository.save(assignment));
   }

   @Transactional
   public void unassign(Long id) {
      if (!assignmentRepository.existsById(id)) {
         throw ResourceNotFoundException.of("ClassTeacherAssignment", id);
      }
      assignmentRepository.deleteById(id);
   }
}