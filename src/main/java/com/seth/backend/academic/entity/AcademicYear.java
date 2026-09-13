package com.seth.backend.academic.entity;

import com.seth.backend.common.entity.CreatedOnlyEntity;
import com.seth.backend.school.entity.School;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "academic_years")
public class AcademicYear extends CreatedOnlyEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_id", nullable = false)
   private School school;

   @Column(nullable = false, length = 20)
   private String name;

   @Column(name = "start_date", nullable = false)
   private LocalDate startDate;

   @Column(name = "end_date", nullable = false)
   private LocalDate endDate;

   @Column(name = "is_current", nullable = false)
   private boolean current = false;
}