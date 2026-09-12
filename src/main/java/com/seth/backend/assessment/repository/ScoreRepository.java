package com.seth.backend.assessment.repository;

import com.seth.backend.assessment.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {

   @EntityGraph(attributePaths = {"studentEnrollment", "studentEnrollment.student"})
   List<Score> findByAssessment_Id(Long assessmentId);

   @EntityGraph(attributePaths = {"assessment", "assessment.subject"})
   Page<Score> findByStudentEnrollment_IdOrderByCreatedAtDesc(Long studentEnrollmentId, Pageable pageable);

   Optional<Score> findByAssessment_IdAndStudentEnrollment_Id(Long assessmentId, Long studentEnrollmentId);
}