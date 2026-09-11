package com.seth.backend.subject.repository;

import com.seth.backend.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
   boolean existsByCode(String code);
}