package com.seth.backend.user.controller;

import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.security.PermissionConstants;
import com.seth.backend.user.dto.UserCreateRequest;
import com.seth.backend.user.dto.UserResponse;
import com.seth.backend.user.dto.UserRoleAssignRequest;
import com.seth.backend.user.dto.UserStatusUpdateRequest;
import com.seth.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

   private final UserService userService;

   @GetMapping
   @PreAuthorize(PermissionConstants.USER_READ)
   public PageResponse<UserResponse> list(
           @RequestParam(required = false) String search,
           @PageableDefault(size = 20, sort = "username") Pageable pageable) {
      return PageResponse.from(userService.list(search, PageableUtils.capped(pageable)));
   }

   @GetMapping("/{id}")
   @PreAuthorize(PermissionConstants.USER_READ)
   public UserResponse getById(@PathVariable Long id) {
      return userService.getById(id);
   }

   @PostMapping
   @PreAuthorize(PermissionConstants.USER_MANAGE)
   public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
      UserResponse created = userService.create(request);
      return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
   }

   @PatchMapping("/{id}/status")
   @PreAuthorize(PermissionConstants.USER_MANAGE)
   public UserResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusUpdateRequest request) {
      return userService.updateStatus(id, request);
   }

   @PutMapping("/{id}/roles")
   @PreAuthorize(PermissionConstants.ROLE_MANAGE)
   public UserResponse assignRoles(@PathVariable Long id, @Valid @RequestBody UserRoleAssignRequest request) {
      return userService.assignRoles(id, request);
   }
}