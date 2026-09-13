package com.seth.backend.audit.entity;

import com.seth.backend.common.entity.CreatedOnlyEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Append-only row per V7's audit_logs table. Deliberately has no FK to User
 * as a JPA relationship (only user_id, ON DELETE SET NULL at the DB level) —
 * an audit trail must survive the referenced user being deleted, and we
 * never want a lazy-load or cascade to touch this table.
 */
@Getter
@Setter
@Entity
@Table(name = "audit_logs")
public class AuditLog extends CreatedOnlyEntity {

   @Column(name = "user_id")
   private Long userId;

   @Column(nullable = false, length = 100)
   private String action;

   @Column(name = "entity_type", length = 50)
   private String entityType;

   @Column(name = "entity_id")
   private Long entityId;

   /**
    * Raw JSON text, written pre-serialized by the caller (see AuditLogService).
    * Kept as String rather than a Map so this entity never needs a Jackson
    * dependency on the read side and the write side controls exactly what's
    * serialized (no accidental leakage of full entities into the log).
    */
   @JdbcTypeCode(SqlTypes.JSON)
   @Column(columnDefinition = "jsonb")
   private String details;

   @Column(name = "ip_address", length = 45)
   private String ipAddress;
}