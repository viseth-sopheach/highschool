package com.seth.backend.student.repository;

import com.seth.backend.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

   @EntityGraph(attributePaths = "user")
   @Query("""
           select s from Student s
           where (:search is null
                  or lower(s.khmerName) like lower(concat('%', :search, '%'))
                  or lower(s.englishName) like lower(concat('%', :search, '%'))
                  or lower(s.studentCode) like lower(concat('%', :search, '%')))
           """)
   Page<Student> search(@Param("search") String search, Pageable pageable);

   @EntityGraph(attributePaths = "user")
   Optional<Student> findWithUserById(Long id);

   @EntityGraph(attributePaths = "user")
   Optional<Student> findWithUserByUserId(Long userId);

   boolean existsByStudentCode(String studentCode);

   boolean existsByUserId(Long userId);
}