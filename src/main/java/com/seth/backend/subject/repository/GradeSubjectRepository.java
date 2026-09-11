package com.seth.backend.subject.repository;

import com.seth.backend.subject.entity.GradeSubject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeSubjectRepository extends JpaRepository<GradeSubject, Long> {

   @EntityGraph(attributePaths = {"subject", "grade", "studyTrack"})
   List<GradeSubject> findByGrade_IdAndStudyTrack_Id(Long gradeId, Long studyTrackId);

   @EntityGraph(attributePaths = {"subject", "grade"})
   List<GradeSubject> findByGrade_IdAndStudyTrackIsNull(Long gradeId);

   boolean existsByGrade_IdAndStudyTrack_IdAndSubject_Id(Long gradeId, Long studyTrackId, Long subjectId);

   boolean existsByGrade_IdAndStudyTrackIsNullAndSubject_Id(Long gradeId, Long subjectId);
}