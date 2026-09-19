package com.seth.backend.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Per-JVM fixed-window limiter. Good enough for a single-instance deployment;
 * move to Redis INCR/EXPIRE if this ever runs behind a load balancer with
 * multiple app instances, since Caffeine state isn't shared across them.
 */
@Component
public class LoginRateLimiter {

   private static final int MAX_ATTEMPTS_PER_WINDOW = 10;
   private static final Duration WINDOW = Duration.ofMinutes(1);

   private final Cache<String, AtomicInteger> attempts = Caffeine.newBuilder()
           .expireAfterWrite(WINDOW)
           .maximumSize(100_000)
           .build();

   /** @return true if this call is within the allowed rate, false if it should be rejected. */
   public boolean tryAcquire(String key) {
      return attempts.get(key, k -> new AtomicInteger(0)).incrementAndGet() <= MAX_ATTEMPTS_PER_WINDOW;
   }
}