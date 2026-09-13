package com.seth.backend.school.repository;

import com.seth.backend.school.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
   boolean existsByCode(String code);
   Page<School> findAll(Pageable pageable);
}