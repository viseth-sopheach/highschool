package com.seth.backend.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seth.backend.audit.entity.AuditLog;
import com.seth.backend.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * Every write uses REQUIRES_NEW: a failed login must still be recorded even
 * though the surrounding business transaction may roll back — audit rows
 * must never depend on the outcome of the action they're describing.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

   private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

   private final AuditLogRepository auditLogRepository;
   private final ObjectMapper objectMapper;

   public void record(Long userId, String action, String entityType, Long entityId, Map<String, ?> details) {
      AuditLog entry = new AuditLog();
      entry.setUserId(userId);
      entry.setAction(action);
      entry.setEntityType(entityType);
      entry.setEntityId(entityId);
      entry.setDetails(toJson(details));
      entry.setIpAddress(currentClientIp());
      persist(entry);
   }

   public void record(Long userId, String action) {
      record(userId, action, null, null, null);
   }

   public void record(Long userId, String action, String entityType, Long entityId) {
      record(userId, action, entityType, entityId, null);
   }

   @Transactional(propagation = Propagation.REQUIRES_NEW)
   protected void persist(AuditLog entry) {
      auditLogRepository.save(entry);
   }

   private String toJson(Map<String, ?> details) {
      if (details == null || details.isEmpty()) {
         return null;
      }
      try {
         return objectMapper.writeValueAsString(details);
      } catch (JsonProcessingException e) {
         log.warn("Failed to serialize audit details for entry, continuing without them", e);
         return null;
      }
   }

   private String currentClientIp() {
      ServletRequestAttributes attrs =
              (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attrs == null) {
         return null;
      }
      HttpServletRequest request = attrs.getRequest();
      String forwarded = request.getHeader("X-Forwarded-For");
      if (forwarded != null && !forwarded.isBlank()) {
         return forwarded.split(",")[0].trim();
      }
      return request.getRemoteAddr();
   }
}