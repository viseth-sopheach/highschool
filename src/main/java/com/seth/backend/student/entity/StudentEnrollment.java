package com.seth.backend.student.entity;

import com.seth.backend.common.entity.BaseEntity;
import com.seth.backend.schoolclass.entity.SchoolClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "student_enrollments")
public class StudentEnrollment extends BaseEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "student_id", nullable = false)
   private Student student;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_class_id", nullable = false)
   private SchoolClass schoolClass;

   @Column(name = "enrollment_date", nullable = false)
   private LocalDate enrollmentDate = LocalDate.now();

   @Enumerated(EnumType.STRING)
   @Column(nullable = false, length = 20)
   private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
}