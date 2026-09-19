package com.seth.backend.academic.entity;

import com.seth.backend.common.entity.IdentityEntity;
import com.seth.backend.school.entity.School;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

@Getter
@Setter
@Filter(name = "schoolFilter", condition = "school_id = :schoolId")
@Entity
@Table(name = "grading_scales")
public class GradingScale extends IdentityEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_id", nullable = false)
   private School school;

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