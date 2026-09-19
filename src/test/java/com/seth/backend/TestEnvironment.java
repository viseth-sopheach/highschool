package com.seth.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class TestEnvironment {

   private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

   @DynamicPropertySource
   static void registerEnvironmentProperties(DynamicPropertyRegistry registry) {
      register(registry, "spring.datasource.url", "DB_URL");
      register(registry, "spring.datasource.username", "DB_USERNAME");
      register(registry, "spring.datasource.password", "DB_PASSWORD");
      register(registry, "app.jwt.secret", "JWT_SECRET");
      register(registry, "app.google.client-id", "GOOGLE_CLIENT_ID");
      registry.add("app.cors.allowed-origins[0]", () -> "http://localhost:5173");
   }

   private static void register(DynamicPropertyRegistry registry, String propertyName, String envName) {
      registry.add(propertyName, () -> DOTENV.get(envName, System.getenv(envName)));
   }
}
