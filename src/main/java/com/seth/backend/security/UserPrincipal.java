package com.seth.backend.security;

import com.seth.backend.user.entity.Permission;
import com.seth.backend.user.entity.Role;
import com.seth.backend.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserPrincipal implements UserDetails {

   private final Long id;
   private final String username;
   private final String passwordHash;
   private final boolean enabled;
   private final boolean accountNonLocked;
   private final Set<GrantedAuthority> authorities;

   public UserPrincipal(User user) {
      this.id = user.getId();
      this.username = user.getUsername();
      this.passwordHash = user.getPasswordHash();
      this.enabled = user.getStatus() == com.seth.backend.user.entity.UserStatus.ACTIVE;
      this.accountNonLocked = user.getStatus() != com.seth.backend.user.entity.UserStatus.LOCKED;

      Stream<GrantedAuthority> roleAuthorities = user.getRoles().stream()
              .map(Role::getName)
              .map(name -> "ROLE_" + name)
              .map(SimpleGrantedAuthority::new);

      Stream<GrantedAuthority> permAuthorities = user.getRoles().stream()
              .flatMap(r -> r.getPermissions().stream())
              .map(Permission::getName)
              .map(name -> "PERM_" + name)
              .map(SimpleGrantedAuthority::new);

      this.authorities = Stream.concat(roleAuthorities, permAuthorities).collect(Collectors.toSet());
   }

   public Long getId() { return id; }

   @Override public Set<GrantedAuthority> getAuthorities() { return authorities; }
   @Override public String getPassword() { return passwordHash; }
   @Override public String getUsername() { return username; }
   @Override public boolean isAccountNonExpired() { return true; }
   @Override public boolean isAccountNonLocked() { return accountNonLocked; }
   @Override public boolean isCredentialsNonExpired() { return true; }
   @Override public boolean isEnabled() { return enabled; }
}