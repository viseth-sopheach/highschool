package com.seth.backend.academic.entity;

import com.seth.backend.common.entity.IdentityEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "grading_scales")
public class GradingScale extends IdentityEntity {

   @Column(name = "min_score", nullable = false)
   private BigDecimal minScore;

   @Column(name = "max_score", nullable = false)
   private BigDecimal maxScore;

   @Column(name = "letter_grade", nullable = false, length = 5)
   private String letterGrade;

   @Column(name = "gpa_point")
   private BigDecimal gpaPoint;

   @Column(length = 100)
   private String description;
}