package com.seth.backend.subject.service;

import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.subject.dto.SubjectRequest;
import com.seth.backend.subject.dto.SubjectResponse;
import com.seth.backend.subject.entity.Subject;
import com.seth.backend.subject.mapper.SubjectMapper;
import com.seth.backend.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

   private final SubjectRepository subjectRepository;
   private final SubjectMapper subjectMapper;

   public List<SubjectResponse> list() {
      return subjectRepository.findAll().stream().map(subjectMapper::toResponse).toList();
   }

   public SubjectResponse getById(Long id) {
      return subjectMapper.toResponse(subjectRepository.findById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Subject", id)));
   }

   @Transactional
   public SubjectResponse create(SubjectRequest request) {
      if (subjectRepository.existsByCode(request.code())) {
         throw new DuplicateResourceException("Subject code '" + request.code() + "' already exists.");
      }
      Subject subject = new Subject();
      subject.setCode(request.code());
      subject.setNameKm(request.nameKm());
      subject.setNameEn(request.nameEn());
      subject.setActive(request.active());
      return subjectMapper.toResponse(subjectRepository.save(subject));
   }

   @Transactional
   public SubjectResponse update(Long id, SubjectRequest request) {
      Subject subject = subjectRepository.findById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("Subject", id));

      if (!subject.getCode().equalsIgnoreCase(request.code())
              && subjectRepository.existsByCode(request.code())) {
         throw new DuplicateResourceException("Subject code '" + request.code() + "' already exists.");
      }

      subject.setCode(request.code());
      subject.setNameKm(request.nameKm());
      subject.setNameEn(request.nameEn());
      subject.setActive(request.active());
      return subjectMapper.toResponse(subject);
   }

   @Transactional
   public void delete(Long id) {
      if (!subjectRepository.existsById(id)) {
         throw ResourceNotFoundException.of("Subject", id);
      }
      subjectRepository.deleteById(id);
   }
}