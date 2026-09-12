package com.seth.backend.assessment.entity;

import com.seth.backend.common.entity.BaseEntity;
import com.seth.backend.student.entity.StudentEnrollment;
import com.seth.backend.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "scores")
public class Score extends BaseEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "assessment_id", nullable = false)
   private Assessment assessment;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "student_enrollment_id", nullable = false)
   private StudentEnrollment studentEnrollment;

   @Column(nullable = false)
   private BigDecimal score;

   @Column(length = 255)
   private String remarks;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "recorded_by", nullable = false)
   private Teacher recordedBy;
}