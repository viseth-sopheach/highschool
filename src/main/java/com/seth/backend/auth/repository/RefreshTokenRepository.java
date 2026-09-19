package com.seth.backend.auth.repository;

import com.seth.backend.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
   Optional<RefreshToken> findByTokenHash(String tokenHash);
   @Modifying
   @Query("update RefreshToken t set t.revoked = true where t.user.id = :userId and t.revoked = false")
   int revokeAllActiveForUser(@Param("userId") Long userId);
}