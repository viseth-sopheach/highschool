package com.seth.backend.audit.repository;

import com.seth.backend.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

   /**
    * idx_audit_logs_entity (entity_type, entity_id) and idx_audit_logs_user /
    * idx_audit_logs_created (V7/V10) cover every branch here — no filter on
    * this query runs a sequential scan even once the table is in the millions
    * of rows, which an append-only audit table reaches long before any other
    * table in this schema.
    */
   @Query("""
           select a from AuditLog a
           where (:userId is null or a.userId = :userId)
             and (:entityType is null or a.entityType = :entityType)
             and (:entityId is null or a.entityId = :entityId)
           order by a.createdAt desc
           """)
   Page<AuditLog> search(@Param("userId") Long userId,
                         @Param("entityType") String entityType,
                         @Param("entityId") Long entityId,
                         Pageable pageable);
}