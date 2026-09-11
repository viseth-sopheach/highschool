package com.seth.backend.subject.service;

import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.schoolclass.repository.SchoolClassRepository;
import com.seth.backend.subject.dto.ClassSubjectRequest;
import com.seth.backend.subject.dto.ClassSubjectResponse;
import com.seth.backend.subject.entity.ClassSubject;
import com.seth.backend.subject.entity.Subject;
import com.seth.backend.subject.mapper.ClassSubjectMapper;
import com.seth.backend.subject.repository.ClassSubjectRepository;
import com.seth.backend.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassSubjectService {

   private final ClassSubjectRepository classSubjectRepository;
   private final SchoolClassRepository schoolClassRepository;
   private final SubjectRepository subjectRepository;
   private final ClassSubjectMapper mapper;

   public List<ClassSubjectResponse> listForClass(Long schoolClassId) {
      return classSubjectRepository.findBySchoolClass_Id(schoolClassId).stream()
              .map(mapper::toResponse).toList();
   }

   @Transactional
   public ClassSubjectResponse assign(Long schoolClassId, ClassSubjectRequest request) {
      SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", schoolClassId));
      Subject subject = subjectRepository.findById(request.subjectId())
              .orElseThrow(() -> ResourceNotFoundException.of("Subject", request.subjectId()));

      if (classSubjectRepository.existsBySchoolClass_IdAndSubject_Id(schoolClassId, request.subjectId())) {
         throw new DuplicateResourceException("Subject already assigned to this class.");
      }

      ClassSubject cs = new ClassSubject();
      cs.setSchoolClass(schoolClass);
      cs.setSubject(subject);
      return mapper.toResponse(classSubjectRepository.save(cs));
   }

   @Transactional
   public void unassign(Long id) {
      if (!classSubjectRepository.existsById(id)) {
         throw ResourceNotFoundException.of("ClassSubject", id);
      }
      classSubjectRepository.deleteById(id);
   }
}