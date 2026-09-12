package com.seth.backend.assessment.repository;

import com.seth.backend.assessment.entity.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

   @EntityGraph(attributePaths = {"subject", "assessmentType", "schoolClass"})
   @Query("""
           select a from Assessment a
           where (:schoolClassId is null or a.schoolClass.id = :schoolClassId)
             and (:subjectId is null or a.subject.id = :subjectId)
           """)
   Page<Assessment> search(@Param("schoolClassId") Long schoolClassId,
                           @Param("subjectId") Long subjectId,
                           Pageable pageable);

   @EntityGraph(attributePaths = {"subject", "assessmentType", "schoolClass"})
   Optional<Assessment> findWithRelationsById(Long id);
}