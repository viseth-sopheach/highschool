package com.seth.backend.academic.entity;

import com.seth.backend.common.entity.CreatedOnlyEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "academic_years")
public class AcademicYear extends CreatedOnlyEntity {

   @Column(nullable = false, unique = true, length = 20)
   private String name;

   @Column(name = "start_date", nullable = false)
   private LocalDate startDate;

   @Column(name = "end_date", nullable = false)
   private LocalDate endDate;

   @Column(name = "is_current", nullable = false)
   private boolean current = false;
}