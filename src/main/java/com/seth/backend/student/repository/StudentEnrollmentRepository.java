package com.seth.backend.student.repository;

import com.seth.backend.student.entity.EnrollmentStatus;
import com.seth.backend.student.entity.StudentEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {

   @EntityGraph(attributePaths = {"schoolClass", "schoolClass.academicYear", "schoolClass.grade", "schoolClass.studyTrack"})
   Page<StudentEnrollment> findByStudent_Id(Long studentId, Pageable pageable);

   @EntityGraph(attributePaths = {"student", "student.user"})
   Page<StudentEnrollment> findBySchoolClass_Id(Long schoolClassId, Pageable pageable);

   /** Backed by uq_one_active_enrollment_per_year (V13) — DB now enforces this too. */
   boolean existsByStudent_IdAndAcademicYear_IdAndStatus(
           Long studentId, Long academicYearId, EnrollmentStatus status);

   @EntityGraph(attributePaths = {"schoolClass", "student"})
   Optional<StudentEnrollment> findWithRelationsById(Long id);

   boolean existsBySchoolClass_IdAndClassPresidentTrue(Long schoolClassId);
}