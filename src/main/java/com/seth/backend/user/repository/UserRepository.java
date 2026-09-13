package com.seth.backend.user.repository;

import com.seth.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

   @EntityGraph(attributePaths = {"roles", "roles.permissions"})
   Optional<User> findWithRolesAndPermissionsByUsername(String username);

   @EntityGraph(attributePaths = "roles")
   Optional<User> findWithRolesById(Long id);

   @EntityGraph(attributePaths = "roles")
   @Query("""
           select u from User u
           where u.school.id = :schoolId
             and (:search is null
                  or lower(u.username) like lower(concat('%', :search, '%'))
                  or lower(u.email) like lower(concat('%', :search, '%')))
           """)
   Page<User> searchBySchool(@Param("schoolId") Long schoolId, @Param("search") String search, Pageable pageable);

   boolean existsByUsername(String username);

   boolean existsByEmail(String email);
}