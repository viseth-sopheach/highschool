package com.seth.backend.assignment.repository;

import com.seth.backend.assignment.entity.ClassTeacherAssignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassTeacherAssignmentRepository extends JpaRepository<ClassTeacherAssignment, Long> {

   @EntityGraph(attributePaths = {"teacher", "teacher.user", "subject"})
   List<ClassTeacherAssignment> findBySchoolClass_Id(Long schoolClassId);

   @EntityGraph(attributePaths = {"schoolClass", "subject"})
   List<ClassTeacherAssignment> findByTeacher_Id(Long teacherId);

   boolean existsBySchoolClass_IdAndHomeroomTrue(Long schoolClassId);

   boolean existsBySchoolClass_IdAndTeacher_IdAndSubject_Id(Long schoolClassId, Long teacherId, Long subjectId);

   /** Handles the nullable-subject uniqueness gap the same way school_classes does. */
   boolean existsBySchoolClass_IdAndTeacher_IdAndSubjectIsNull(Long schoolClassId, Long teacherId);
}