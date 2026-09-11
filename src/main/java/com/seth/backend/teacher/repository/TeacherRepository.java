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

   /**
    * {@code :search} matches teacher_code/khmer_name/english_name via a
    * substring LIKE, which needs the pg_trgm GIN indexes from V9 to stay
    * fast once the table grows past a few thousand rows — a plain B-tree
    * index can't serve a leading-wildcard LIKE.
    */
   @EntityGraph(attributePaths = "user")
   @Query("""
           select t from Teacher t
           where (:search is null
                  or lower(t.khmerName) like lower(concat('%', :search, '%'))
                  or lower(t.englishName) like lower(concat('%', :search, '%'))
                  or lower(t.teacherCode) like lower(concat('%', :search, '%')))
           """)
   Page<Teacher> search(@Param("search") String search, Pageable pageable);

   @EntityGraph(attributePaths = "user")
   Optional<Teacher> findWithUserById(Long id);

   @EntityGraph(attributePaths = "user")
   Optional<Teacher> findWithUserByUserId(Long userId);

   boolean existsByTeacherCode(String teacherCode);

   boolean existsByUserId(Long userId);
}