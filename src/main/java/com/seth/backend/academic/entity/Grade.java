package com.seth.backend.academic.entity;

import com.seth.backend.common.entity.IdentityEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "grades")
public class Grade extends IdentityEntity {

   @Column(nullable = false, unique = true, length = 20)
   private String name;

   @Column(nullable = false, unique = true)
   private Short level;

   @Column(name = "requires_track", nullable = false)
   private boolean requiresTrack = false;
}