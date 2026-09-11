package com.seth.backend.schoolclass.repository;

import com.seth.backend.schoolclass.entity.SchoolClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

   @EntityGraph(attributePaths = {"academicYear", "grade", "studyTrack"})
   @Query("""
           select c from SchoolClass c
           where (:academicYearId is null or c.academicYear.id = :academicYearId)
             and (:gradeId is null or c.grade.id = :gradeId)
             and (:search is null or lower(c.name) like lower(concat('%', :search, '%')))
           """)
   Page<SchoolClass> search(
           @Param("academicYearId") Long academicYearId,
           @Param("gradeId") Long gradeId,
           @Param("search") String search,
           Pageable pageable);

   @EntityGraph(attributePaths = {"academicYear", "grade", "studyTrack"})
   Optional<SchoolClass> findWithRelationsById(Long id);

   boolean existsByAcademicYearIdAndGradeIdAndStudyTrackIdAndName(
           Long academicYearId, Long gradeId, Long studyTrackId, String name);

   /**
    * uq_school_classes_identity is a plain UNIQUE constraint over a nullable
    * study_track_id column — Postgres treats NULL <> NULL, so that DB
    * constraint alone won't stop two "no-track" classes from getting the
    * same (year, grade, name). This app-level check closes that gap.
    */
   boolean existsByAcademicYearIdAndGradeIdAndStudyTrackIdIsNullAndName(
           Long academicYearId, Long gradeId, String name);
}