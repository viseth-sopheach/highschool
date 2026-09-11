package com.seth.backend.subject.repository;

import com.seth.backend.subject.entity.ClassSubject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassSubjectRepository extends JpaRepository<ClassSubject, Long> {

   @EntityGraph(attributePaths = "subject")
   List<ClassSubject> findBySchoolClass_Id(Long schoolClassId);

   boolean existsBySchoolClass_IdAndSubject_Id(Long schoolClassId, Long subjectId);
}