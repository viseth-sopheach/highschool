package com.seth.backend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.seth.backend.common.entity.CreatedOnlyEntity;
import com.seth.backend.user.entity.User;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends CreatedOnlyEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

   @Column(name = "token_hash", nullable = false, unique = true, length = 255)
   private String tokenHash;

   @Column(name = "expires_at", nullable = false)
   private OffsetDateTime expiresAt;

   @Column(nullable = false)
   private boolean revoked = false;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "replaced_by_id")
   private RefreshToken replacedBy;
}