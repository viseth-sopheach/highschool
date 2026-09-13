package com.seth.backend.academic.entity;

import com.seth.backend.common.entity.IdentityEntity;
import com.seth.backend.school.entity.School;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "study_tracks")
public class StudyTrack extends IdentityEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_id", nullable = false)
   private School school;

   @Column(nullable = false, length = 30)
   private String code;

   @Column(name = "name_km", nullable = false, length = 100)
   private String nameKm;

   @Column(name = "name_en", nullable = false, length = 100)
   private String nameEn;
}