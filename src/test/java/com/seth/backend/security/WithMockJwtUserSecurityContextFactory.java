package com.seth.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.Map;

class WithMockJwtUserSecurityContextFactory implements WithSecurityContextFactory<WithMockJwtUser> {

   @Autowired
   private JdbcTemplate jdbcTemplate;

   @Override
   public SecurityContext createSecurityContext(WithMockJwtUser annotation) {
      Map<String, Object> user = jdbcTemplate.queryForMap(
              "select id, school_id from users where username = ?",
              annotation.username());
      var authorities = Arrays.stream(annotation.authorities())
              .map(SimpleGrantedAuthority::new)
              .toList();

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(new JwtAuthenticationToken(
              ((Number) user.get("id")).longValue(),
              ((Number) user.get("school_id")).longValue(),
              annotation.username(),
              authorities));
      return context;
   }
}
