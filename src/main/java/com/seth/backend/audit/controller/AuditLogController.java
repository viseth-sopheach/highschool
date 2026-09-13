package com.seth.backend.audit.controller;

import com.seth.backend.audit.dto.AuditLogResponse;
import com.seth.backend.audit.repository.AuditLogRepository;
import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.security.PermissionConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

   private final AuditLogRepository auditLogRepository;

   @GetMapping
   @PreAuthorize(PermissionConstants.AUDIT_READ)
   public PageResponse<AuditLogResponse> search(
           @RequestParam(required = false) Long userId,
           @RequestParam(required = false) String entityType,
           @RequestParam(required = false) Long entityId,
           @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
      return PageResponse.from(auditLogRepository
              .search(userId, entityType, entityId, PageableUtils.capped(pageable))
              .map(a -> new AuditLogResponse(a.getId(), a.getUserId(), a.getAction(),
                      a.getEntityType(), a.getEntityId(), a.getDetails(), a.getIpAddress(), a.getCreatedAt())));
   }
}