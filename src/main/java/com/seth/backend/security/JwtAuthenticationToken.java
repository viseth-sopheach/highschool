package com.seth.backend.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

   private final Long userId;
   private final Long schoolId;
   private final String username;

   public JwtAuthenticationToken(Long userId, Long schoolId, String username,
                                 Collection<? extends GrantedAuthority> authorities) {
      super(authorities);
      this.userId = userId;
      this.schoolId = schoolId;
      this.username = username;
      setAuthenticated(true);
   }

   @Override
   public Object getCredentials() {
      return null;
   }

   @Override
   public Object getPrincipal() {
      return username;
   }

   public Long getUserId() {
      return userId;
   }

   public Long getSchoolId() {
      return schoolId;
   }
}