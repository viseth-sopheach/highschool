package com.seth.backend.assessment.repository;

import com.seth.backend.assessment.entity.AssessmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentTypeRepository extends JpaRepository<AssessmentType, Long> {
}