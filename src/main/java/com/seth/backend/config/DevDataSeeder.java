package com.seth.backend.config;

import com.seth.backend.school.entity.School;
import com.seth.backend.school.repository.SchoolRepository;
import com.seth.backend.user.entity.Role;
import com.seth.backend.user.entity.User;
import com.seth.backend.user.entity.UserStatus;
import com.seth.backend.user.repository.RoleRepository;
import com.seth.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder {

   private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
   private static final String DEFAULT_SCHOOL_CODE = "DEFAULT";

   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final SchoolRepository schoolRepository;
   private final PasswordEncoder passwordEncoder;

   @Value("${app.dev-admin.username:viseth@gmail.com}")
   private String devAdminUsername;

   @Value("${app.dev-admin.email:viseth@gmail.com}")
   private String devAdminEmail;

   @Value("${app.dev-admin.password:Admin@123}")
   private String devAdminPassword;

   @Bean
   @Transactional
   public CommandLineRunner seedInitialPrincipal() {
      return args -> {
         if (userRepository.existsByUsername(devAdminUsername)) {
            return;
         }

         School school = schoolRepository.findAll().stream()
                 .filter(s -> DEFAULT_SCHOOL_CODE.equals(s.getCode()))
                 .findFirst()
                 .orElseThrow(() -> new IllegalStateException(
                         "Default school missing — check V14 migration ran before startup"));

         Role principalRole = roleRepository.findByName("PRINCIPAL")
                 .orElseThrow(() -> new IllegalStateException(
                         "PRINCIPAL role missing — check V1/V8 migrations ran before startup"));

         User admin = new User();
         admin.setSchool(school);
         admin.setUsername(devAdminUsername);
         admin.setEmail(devAdminEmail);
         admin.setPasswordHash(passwordEncoder.encode(devAdminPassword));
         admin.setStatus(UserStatus.ACTIVE);
         admin.setRoles(Set.of(principalRole));
         userRepository.save(admin);

         log.warn("=================================================================");
         log.warn(" Seeded dev PRINCIPAL account -> username: {} / password: {}", devAdminUsername, devAdminPassword);
         log.warn(" PRINCIPAL has every permission (V8 wildcard grant) — full route access.");
         log.warn(" This only ever runs once, and only in the 'dev' profile.");
         log.warn("=================================================================");
      };
   }
}