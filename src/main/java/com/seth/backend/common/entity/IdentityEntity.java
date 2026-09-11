package com.seth.backend.common.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base for lookup/reference tables that have no audit columns at all —
 * just {@code id BIGINT GENERATED ALWAYS AS IDENTITY} (e.g. grades,
 * study_tracks). Distinct from {@link BaseEntity} (has created_at/updated_at
 * + update trigger) and {@link CreatedOnlyEntity} (has created_at only).
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdentityEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
}