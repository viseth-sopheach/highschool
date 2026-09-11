package com.seth.backend.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.seth.backend.common.entity.BaseEntity;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

   @Column(nullable = false, unique = true, length = 100)
   private String username;

   @Column(unique = true, length = 255)
   private String email;

   @Column(name = "password_hash", nullable = false, length = 255)
   private String passwordHash;

   @Enumerated(EnumType.STRING)
   @Column(nullable = false, length = 20)
   private UserStatus status = UserStatus.ACTIVE;

   @Column(name = "failed_login_attempts", nullable = false)
   private short failedLoginAttempts = 0;

   @Column(name = "locked_until")
   private OffsetDateTime lockedUntil;

   @Column(name = "last_login_at")
   private OffsetDateTime lastLoginAt;

   @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(
           name = "user_roles",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "role_id")
   )
   private Set<Role> roles = new HashSet<>();
}