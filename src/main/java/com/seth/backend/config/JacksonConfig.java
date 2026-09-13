package com.seth.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defined explicitly rather than relying on Spring Boot's JacksonAutoConfiguration
 * to supply this bean — that autoconfiguration wasn't firing in this project
 * (likely due to the split-out webmvc/json starters not pulling it in the way
 * spring-boot-starter-web historically did), and several components
 * (AuditLogService, and every controller returning OffsetDateTime/LocalDate
 * fields) depend on one being present.
 */
@Configuration
public class JacksonConfig {

   @Bean
   public ObjectMapper objectMapper() {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      // Without this, OffsetDateTime/LocalDate serialize as numeric arrays
      // (e.g. [2026,9,13]) instead of ISO-8601 strings — breaks every DTO
      // in this app that carries a date/time field.
      mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return mapper;
   }
}