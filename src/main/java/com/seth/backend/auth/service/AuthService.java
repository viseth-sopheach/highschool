package com.seth.backend.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.seth.backend.audit.AuditActions;
import com.seth.backend.audit.service.AuditLogService;
import com.seth.backend.auth.dto.AuthResponse;
import com.seth.backend.auth.dto.GoogleLoginRequest;
import com.seth.backend.auth.dto.LoginRequest;
import com.seth.backend.auth.dto.RefreshRequest;
import com.seth.backend.auth.entity.RefreshToken;
import com.seth.backend.auth.repository.RefreshTokenRepository;
import com.seth.backend.config.JwtProperties;
import com.seth.backend.exception.AccountLockedException;
import com.seth.backend.exception.AccountPendingApprovalException;
import com.seth.backend.exception.InvalidTokenException;
import com.seth.backend.school.entity.School;
import com.seth.backend.school.repository.SchoolRepository;
import com.seth.backend.security.JwtService;
import com.seth.backend.security.UserPrincipal;
import com.seth.backend.user.entity.User;
import com.seth.backend.user.entity.UserStatus;
import com.seth.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

   private static final int MAX_FAILED_ATTEMPTS = 5;
   private static final long LOCK_DURATION_MINUTES = 15;

   private final AuthenticationManager authenticationManager;
   private final UserRepository userRepository;
   private final RefreshTokenRepository refreshTokenRepository;
   private final JwtService jwtService;
   private final JwtProperties jwtProperties;
   private final AuditLogService auditLogService;
   private final GoogleIdTokenVerifier googleIdTokenVerifier;
   private final SchoolRepository schoolRepository;
   private final PasswordEncoder passwordEncoder;

   /**
    * noRollbackFor(BadCredentialsException) is load-bearing: on bad password
    * this method rethrows BadCredentialsException, and without the override
    * Spring's default unchecked-exception rollback would also undo the
    * failed-attempt counter increment inside registerFailedAttempt() —
    * meaning lockout after N attempts would never actually trigger.
    */
   @Transactional(noRollbackFor = BadCredentialsException.class)
   public AuthResponse login(LoginRequest request) {
      User user = userRepository.findWithRolesAndPermissionsByUsername(request.username())
              .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

      if (user.getStatus() == UserStatus.LOCKED) {
         if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now())) {
            throw new AccountLockedException("Account is locked due to too many failed attempts. Try again later.");
         }
         // Lock window expired — auto-unlock before evaluating credentials.
         user.setStatus(UserStatus.ACTIVE);
         user.setFailedLoginAttempts((short) 0);
         user.setLockedUntil(null);
      } else if (user.getStatus() == UserStatus.PENDING) {
         throw new AccountPendingApprovalException("Your account is awaiting administrator approval.");
      } else if (user.getStatus() == UserStatus.DISABLED) {
         throw new AccountLockedException("This account is disabled. Contact your school administrator.");
      }

      if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
         registerFailedAttempt(user);
         throw new BadCredentialsException("Invalid username or password.");
      }

      user.setFailedLoginAttempts((short) 0);
      user.setLastLoginAt(OffsetDateTime.now());
      userRepository.save(user);
      auditLogService.record(user.getId(), AuditActions.LOGIN_SUCCESS, "User", user.getId());

      UserPrincipal principal = new UserPrincipal(user);
      String accessToken = jwtService.generateAccessToken(principal);
      String refreshToken = issueRefreshToken(user);
      return new AuthResponse(accessToken, refreshToken, jwtProperties.getAccessTokenExpirationMs());
   }

   @Transactional
   public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
      com.google.api.client.googleapis.auth.oauth2.GoogleIdToken idToken;
      try {
         idToken = googleIdTokenVerifier.verify(request.idToken());
      } catch (Exception e) {
         throw new InvalidTokenException("Could not verify Google token.");
      }
      if (idToken == null) {
         throw new InvalidTokenException("Invalid Google token.");
      }

      var payload = idToken.getPayload();
      if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
         throw new InvalidTokenException("Google account email is not verified.");
      }
      String email = payload.getEmail();

      var existing = userRepository.findWithRolesAndPermissionsByEmail(email);
      if (existing.isPresent()) {
         User user = existing.get();
         if (user.getStatus() == UserStatus.PENDING) {
            throw new AccountPendingApprovalException("Your account is awaiting administrator approval.");
         }
         if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountLockedException("This account is not active. Contact your school administrator.");
         }

         user.setLastLoginAt(OffsetDateTime.now());
         userRepository.save(user);
         auditLogService.record(user.getId(), AuditActions.GOOGLE_LOGIN_SUCCESS, "User", user.getId());

         UserPrincipal principal = new UserPrincipal(user);
         String accessToken = jwtService.generateAccessToken(principal);
         String refreshToken = issueRefreshToken(user);
         return new AuthResponse(accessToken, refreshToken, jwtProperties.getAccessTokenExpirationMs());
      }

      School defaultSchool = schoolRepository.findByCode("DEFAULT")
              .orElseThrow(() -> new IllegalStateException("Default school not configured."));

      User user = new User();
      user.setSchool(defaultSchool);
      user.setUsername(email);
      user.setEmail(email);
      user.setPasswordHash(passwordEncoder.encode(generateRawToken()));
      user.setStatus(UserStatus.PENDING);
      userRepository.save(user);

      auditLogService.record(user.getId(), AuditActions.GOOGLE_ACCOUNT_CREATED, "User", user.getId(),
              Map.of("email", email));

      throw new AccountPendingApprovalException(
              "Account created. An administrator must approve and assign your role before you can sign in.");
   }

   @Transactional
   public AuthResponse refresh(RefreshRequest request) {
      String hash = hashToken(request.refreshToken());

      RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
              .orElseThrow(() -> new InvalidTokenException("Refresh token not recognized."));

      if (existing.isRevoked() || existing.getExpiresAt().isBefore(OffsetDateTime.now())) {
         throw new InvalidTokenException("Refresh token expired or revoked.");
      }

      User user = userRepository.findWithRolesAndPermissionsByUsername(
              existing.getUser().getUsername()).orElseThrow(
              () -> new InvalidTokenException("User no longer exists."));

      existing.setRevoked(true);

      UserPrincipal principal = new UserPrincipal(user);
      String newAccessToken = jwtService.generateAccessToken(principal);
      String newRefreshTokenRaw = generateRawToken();
      RefreshToken rotated = saveRefreshToken(user, newRefreshTokenRaw);
      existing.setReplacedBy(rotated);
      refreshTokenRepository.save(existing);
      auditLogService.record(user.getId(), AuditActions.TOKEN_REFRESH, "User", user.getId());

      return new AuthResponse(newAccessToken, newRefreshTokenRaw, jwtProperties.getAccessTokenExpirationMs());
   }

   @Transactional
   public void logout(RefreshRequest request) {
      String hash = hashToken(request.refreshToken());
      refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
         rt.setRevoked(true);
         refreshTokenRepository.save(rt);
         auditLogService.record(rt.getUser().getId(), AuditActions.LOGOUT, "User", rt.getUser().getId());
      });
   }

   private void registerFailedAttempt(User user) {
      short attempts = (short) (user.getFailedLoginAttempts() + 1);
      user.setFailedLoginAttempts(attempts);
      boolean justLocked = attempts >= MAX_FAILED_ATTEMPTS;
      if (justLocked) {
         user.setStatus(UserStatus.LOCKED);
         user.setLockedUntil(OffsetDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
      }
      userRepository.save(user);

      auditLogService.record(user.getId(), AuditActions.LOGIN_FAILURE, "User", user.getId(),
              Map.of("failedAttempts", attempts));
      if (justLocked) {
         auditLogService.record(user.getId(), AuditActions.ACCOUNT_LOCKED, "User", user.getId(),
                 Map.of("lockedUntil", user.getLockedUntil().toString()));
      }
   }

   private String issueRefreshToken(User user) {
      String raw = generateRawToken();
      saveRefreshToken(user, raw);
      return raw;
   }

   private RefreshToken saveRefreshToken(User user, String raw) {
      RefreshToken rt = new RefreshToken();
      rt.setUser(user);
      rt.setTokenHash(hashToken(raw));
      rt.setExpiresAt(OffsetDateTime.now().plus(
              java.time.Duration.ofMillis(jwtProperties.getRefreshTokenExpirationMs())));
      rt.setRevoked(false);
      return refreshTokenRepository.save(rt);
   }

   private String generateRawToken() {
      byte[] bytes = new byte[64];
      new SecureRandom().nextBytes(bytes);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
   }

   private String hashToken(String raw) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
         return HexFormat.of().formatHex(hash);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException("SHA-256 algorithm unavailable", e);
      }
   }
}