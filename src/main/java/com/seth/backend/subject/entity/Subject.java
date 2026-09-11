package com.seth.backend.subject.entity;

import com.seth.backend.common.entity.IdentityEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subjects")
public class Subject extends IdentityEntity {

   @Column(nullable = false, unique = true, length = 20)
   private String code;

   @Column(name = "name_km", nullable = false, length = 100)
   private String nameKm;

   @Column(name = "name_en", nullable = false, length = 100)
   private String nameEn;

   @Column(name = "is_active", nullable = false)
   private boolean active = true;
}