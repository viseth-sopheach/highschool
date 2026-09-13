package com.seth.backend.teacher.repository;

import com.seth.backend.teacher.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

   @EntityGraph(attributePaths = "user")
   @Query("""
           select t from Teacher t
           where t.school.id = :schoolId
             and (:search is null
                  or lower(t.khmerName) like lower(concat('%', :search, '%'))
                  or lower(t.englishName) like lower(concat('%', :search, '%'))
                  or lower(t.teacherCode) like lower(concat('%', :search, '%')))
           """)
   Page<Teacher> search(@Param("schoolId") Long schoolId, @Param("search") String search, Pageable pageable);

   @EntityGraph(attributePaths = "user")
   Optional<Teacher> findWithUserById(Long id);

   @EntityGraph(attributePaths = "user")
   Optional<Teacher> findWithUserByUserId(Long userId);

   boolean existsBySchool_IdAndTeacherCode(Long schoolId, String teacherCode);

   boolean existsByUserId(Long userId);
}