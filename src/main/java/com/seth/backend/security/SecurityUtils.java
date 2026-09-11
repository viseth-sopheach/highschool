package com.seth.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
   private SecurityUtils() {
   }

   private static JwtAuthenticationToken currentToken() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null || !(auth instanceof JwtAuthenticationToken token)) {
         throw new IllegalStateException("No authenticated user in context");
      }
      return token;
   }

   public static Long currentUserId() {
      return currentToken().getUserId();
   }

   public static String currentUsername() {
      return currentToken().getPrincipal().toString();
   }

   public static boolean hasAuthority(String authority) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      return auth != null && auth.getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals(authority));
   }
}