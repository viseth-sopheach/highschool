package com.seth.backend.config;

import com.seth.backend.user.entity.Role;
import com.seth.backend.user.entity.User;
import com.seth.backend.user.entity.UserStatus;
import com.seth.backend.user.repository.RoleRepository;
import com.seth.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

/**
 * Dev-only bootstrap: on first startup, if no "admin" user exists yet,
 * creates one with the PRINCIPAL role and a random password printed once
 * to the logs. This is how you get into the system before any
 * user-management/registration endpoints exist.
 * <p>
 * Deliberately gated to the "dev" profile via {@code @Profile}. Production
 * accounts must be provisioned on purpose (e.g. a one-off admin CLI command
 * or a controlled migration), never auto-seeded on startup.
 */
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder {

   private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
   private static final String SEED_USERNAME = "admin";

   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;

   @Bean
   @Transactional
   public CommandLineRunner seedInitialPrincipal() {
      return args -> {
         if (userRepository.existsByUsername(SEED_USERNAME)) {
            return;
         }

         Role principalRole = roleRepository.findByName("PRINCIPAL")
                 .orElseThrow(() -> new IllegalStateException(
                         "PRINCIPAL role missing — check V1/V8 migrations ran before startup"));

         String rawPassword = generateRandomPassword();

         User admin = new User();
         admin.setUsername(SEED_USERNAME);
         admin.setEmail("admin@seth.local");
         admin.setPasswordHash(passwordEncoder.encode(rawPassword));
         admin.setStatus(UserStatus.ACTIVE);
         admin.setRoles(Set.of(principalRole));
         userRepository.save(admin);

         log.warn("=================================================================");
         log.warn(" Seeded dev admin account -> username: {} / password: {}", SEED_USERNAME, rawPassword);
         log.warn(" This only ever runs once, and only in the 'dev' profile.");
         log.warn("=================================================================");
      };
   }

   private String generateRandomPassword() {
      byte[] bytes = new byte[12];
      new SecureRandom().nextBytes(bytes);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
   }
}