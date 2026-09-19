package com.seth.backend.academic.entity;

import com.seth.backend.common.entity.IdentityEntity;
import com.seth.backend.school.entity.School;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@Filter(name = "schoolFilter", condition = "school_id = :schoolId")
@Entity
@Table(name = "grades")
public class Grade extends IdentityEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_id", nullable = false)
   private School school;

   @Column(nullable = false, length = 20)
   private String name;

   @Column(nullable = false)
   private Short level;

   @Column(name = "requires_track", nullable = false)
   private boolean requiresTrack = false;
}