// src/main/java/com/seth/backend/school/service/SchoolService.java
package com.seth.backend.school.service;

import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.school.dto.SchoolCreateRequest;
import com.seth.backend.school.dto.SchoolResponse;
import com.seth.backend.school.dto.SchoolUpdateRequest;
import com.seth.backend.school.entity.School;
import com.seth.backend.school.mapper.SchoolMapper;
import com.seth.backend.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {

   private final SchoolRepository schoolRepository;
   private final SchoolMapper mapper;

   public Page<SchoolResponse> list(Pageable pageable) {
      return schoolRepository.findAll(pageable).map(mapper::toResponse);
   }

   public SchoolResponse getById(Long id) {
      return mapper.toResponse(schoolRepository.findById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("School", id)));
   }

   @Transactional
   public SchoolResponse create(SchoolCreateRequest request) {
      if (schoolRepository.existsByCode(request.code())) {
         throw new DuplicateResourceException("School code '" + request.code() + "' already exists.");
      }
      School school = new School();
      school.setCode(request.code());
      school.setName(request.name());
      school.setAddress(request.address());
      school.setPhone(request.phone());
      return mapper.toResponse(schoolRepository.save(school));
   }

   @Transactional
   public SchoolResponse update(Long id, SchoolUpdateRequest request) {
      School school = schoolRepository.findById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("School", id));
      school.setName(request.name());
      school.setAddress(request.address());
      school.setPhone(request.phone());
      school.setActive(request.active());
      return mapper.toResponse(school);
   }
}