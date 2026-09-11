package com.seth.backend.user.repository;

import com.seth.backend.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

   @EntityGraph(attributePaths = {"roles", "roles.permissions"})
   Optional<User> findWithRolesAndPermissionsByUsername(String username);

   boolean existsByUsername(String username);

   boolean existsByEmail(String email);
}