package com.seth.backend.assessment.entity;

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
@Table(name = "assessment_types")
public class AssessmentType extends IdentityEntity {

   @Column(nullable = false, unique = true, length = 50)
   private String name;

   @Column(name = "weight_default")
   private BigDecimal weightDefault;
}