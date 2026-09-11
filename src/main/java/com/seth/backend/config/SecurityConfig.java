package com.seth.backend.config;

import com.seth.backend.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

   private final CustomUserDetailsService userDetailsService;
   // private final JwtAuthenticationFilter jwtAuthenticationFilter; // wired in JWT deliverable

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder(12);
   }

   @Bean
   public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
      provider.setPasswordEncoder(passwordEncoder);
      return provider;
   }

   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
      return config.getAuthenticationManager();
   }

   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
              .csrf(csrf -> csrf.disable())
              .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/api/auth/**", "/swagger-ui/**", "/api-docs/**").permitAll()
                      .anyRequest().authenticated()
              );
      // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();
   }
}