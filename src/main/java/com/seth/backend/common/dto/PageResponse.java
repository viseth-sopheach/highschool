package com.seth.backend.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * API-facing shape for paged results. We deliberately don't return Spring
 * Data's {@link Page} directly from controllers — that leaks Spring Data
 * types (and some ugly/unstable field names) into the public contract.
 * Every list endpoint (students, attendance, scores, ...) should return
 * this instead of a raw List, so results stay bounded regardless of how
 * many rows the table holds.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
   public static <T> PageResponse<T> from(Page<T> page) {
      return new PageResponse<>(
              page.getContent(),
              page.getNumber(),
              page.getSize(),
              page.getTotalElements(),
              page.getTotalPages(),
              page.isLast()
      );
   }
}