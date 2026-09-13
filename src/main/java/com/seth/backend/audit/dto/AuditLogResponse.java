package com.seth.backend.audit.dto;

import java.time.OffsetDateTime;

public record AuditLogResponse(
        Long id,
        Long userId,
        String action,
        String entityType,
        Long entityId,
        String details,
        String ipAddress,
        OffsetDateTime createdAt
) {}