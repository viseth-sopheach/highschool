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
@Table(name = "study_tracks")
public class StudyTrack extends IdentityEntity {

   @Column(nullable = false, unique = true, length = 30)
   private String code;

   @Column(name = "name_km", nullable = false, length = 100)
   private String nameKm;

   @Column(name = "name_en", nullable = false, length = 100)
   private String nameEn;
}