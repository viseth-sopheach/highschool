package com.seth.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
   private SecurityUtils() {}

   public static UserPrincipal currentPrincipal() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
         throw new IllegalStateException("No authenticated user in context");
      }
      return principal;
   }

   public static Long currentUserId() {
      return currentPrincipal().getId();
   }

   public static boolean hasAuthority(String authority) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      return auth != null && auth.getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals(authority));
   }
}