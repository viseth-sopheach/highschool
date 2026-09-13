package com.seth.backend.user.service;

import com.seth.backend.audit.AuditActions;
import com.seth.backend.audit.service.AuditLogService;
import com.seth.backend.exception.DuplicateResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.security.SecurityUtils;
import com.seth.backend.user.dto.UserCreateRequest;
import com.seth.backend.user.dto.UserResponse;
import com.seth.backend.user.dto.UserRoleAssignRequest;
import com.seth.backend.user.dto.UserStatusUpdateRequest;
import com.seth.backend.user.entity.Role;
import com.seth.backend.user.entity.User;
import com.seth.backend.user.entity.UserStatus;
import com.seth.backend.user.mapper.UserMapper;
import com.seth.backend.user.repository.RoleRepository;
import com.seth.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final UserMapper userMapper;
   private final PasswordEncoder passwordEncoder;
   private final AuditLogService auditLogService;

   public Page<UserResponse> list(String search, Pageable pageable) {
      String normalized = (search == null || search.isBlank()) ? null : search.trim();
      return userRepository.search(normalized, pageable).map(userMapper::toResponse);
   }

   public UserResponse getById(Long id) {
      User user = userRepository.findWithRolesById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("User", id));
      return userMapper.toResponse(user);
   }

   @Transactional
   public UserResponse create(UserCreateRequest request) {
      if (userRepository.existsByUsername(request.username())) {
         throw new DuplicateResourceException(
                 "Username '" + request.username() + "' is already taken.");
      }
      if (request.email() != null && userRepository.existsByEmail(request.email())) {
         throw new DuplicateResourceException(
                 "Email '" + request.email() + "' is already registered.");
      }

      User user = new User();
      user.setUsername(request.username());
      user.setEmail(request.email());
      user.setPasswordHash(passwordEncoder.encode(request.password()));
      user.setStatus(UserStatus.ACTIVE);
      user.setRoles(resolveRoles(request.roleNames()));

      User saved = userRepository.save(user);
      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.USER_CREATED, "User", saved.getId(),
              Map.of("username", saved.getUsername(), "roles", request.roleNames()));
      return userMapper.toResponse(saved);
   }

   @Transactional
   public UserResponse updateStatus(Long id, UserStatusUpdateRequest request) {
      User user = userRepository.findWithRolesById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("User", id));

      UserStatus previous = user.getStatus();
      user.setStatus(request.status());
      if (request.status() != UserStatus.LOCKED) {
         user.setLockedUntil(null);
         user.setFailedLoginAttempts((short) 0);
      }

      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.USER_STATUS_CHANGED, "User", id,
              Map.of("from", previous, "to", request.status()));
      return userMapper.toResponse(user);
   }

   @Transactional
   public UserResponse assignRoles(Long id, UserRoleAssignRequest request) {
      User user = userRepository.findWithRolesById(id)
              .orElseThrow(() -> ResourceNotFoundException.of("User", id));

      Set<String> previousRoleNames = user.getRoles().stream().map(Role::getName)
              .collect(Collectors.toCollection(TreeSet::new));
      user.setRoles(resolveRoles(request.roleNames()));

      auditLogService.record(SecurityUtils.currentUserId(), AuditActions.USER_ROLES_CHANGED, "User", id,
              Map.of("from", previousRoleNames, "to", request.roleNames()));
      return userMapper.toResponse(user);
   }

   private Set<Role> resolveRoles(Set<String> roleNames) {
      Set<Role> roles = new HashSet<>();
      for (String name : roleNames) {
         roles.add(roleRepository.findByName(name)
                 .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name)));
      }
      return roles;
   }
}