package com.seth.backend.academic.repository;

import com.seth.backend.academic.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
   List<Grade> findAllByOrderByLevelAsc();
}