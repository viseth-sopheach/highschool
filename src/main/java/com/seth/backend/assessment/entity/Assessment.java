package com.seth.backend.assessment.entity;

import com.seth.backend.common.entity.BaseEntity;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.subject.entity.Subject;
import com.seth.backend.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "assessments")
public class Assessment extends BaseEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_class_id", nullable = false)
   private SchoolClass schoolClass;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "subject_id", nullable = false)
   private Subject subject;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "assessment_type_id", nullable = false)
   private AssessmentType assessmentType;

   @Column(nullable = false, length = 150)
   private String title;

   @Column(name = "max_score", nullable = false)
   private BigDecimal maxScore;

   private BigDecimal weight;

   @Column(name = "assessment_date", nullable = false)
   private LocalDate assessmentDate;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "created_by", nullable = false)
   private Teacher createdBy;
}