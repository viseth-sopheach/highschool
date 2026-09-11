package com.seth.backend.auth.controller;

import com.seth.backend.auth.dto.AuthResponse;
import com.seth.backend.auth.dto.LoginRequest;
import com.seth.backend.auth.dto.RefreshRequest;
import com.seth.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

   private final AuthService authService;

   @PostMapping("/login")
   public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
      return ResponseEntity.ok(authService.login(request));
   }

   @PostMapping("/refresh")
   public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
      return ResponseEntity.ok(authService.refresh(request));
   }

   @PostMapping("/logout")
   public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
      authService.logout(request);
      return ResponseEntity.noContent().build();
   }
}