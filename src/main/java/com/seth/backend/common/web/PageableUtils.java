package com.seth.backend.common.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Every paginated list endpoint should run its incoming {@link Pageable}
 * through {@link #capped(Pageable)} before querying. Without a server-side
 * ceiling, a client can request {@code ?size=100000} and force a full table
 * scan/transfer regardless of how good the indexes are — the cap is what
 * actually keeps list endpoints fast as tables grow past a few thousand rows.
 */
public final class PageableUtils {
   private PageableUtils() {}

   private static final int MAX_PAGE_SIZE = 100;

   public static Pageable capped(Pageable pageable) {
      if (pageable.getPageSize() > MAX_PAGE_SIZE) {
         return PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE, pageable.getSort());
      }
      return pageable;
   }
}