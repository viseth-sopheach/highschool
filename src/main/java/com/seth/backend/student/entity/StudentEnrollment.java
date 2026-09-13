package com.seth.backend.student.entity;

import com.seth.backend.academic.entity.AcademicYear;
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

   /**
    * Denormalized from schoolClass.academicYear at insert time. Exists so
    * "one ACTIVE enrollment per student per year" can be enforced by a
    * partial unique index (uq_one_active_enrollment_per_year, V13) instead
    * of only a service-layer check-then-act, which was a real race.
    */
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "academic_year_id", nullable = false)
   private AcademicYear academicYear;

   @Column(name = "enrollment_date", nullable = false)
   private LocalDate enrollmentDate = LocalDate.now();

   @Enumerated(EnumType.STRING)
   @Column(nullable = false, length = 20)
   private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

   /** Scoped to this enrollment (this class, this year) — not a Role. */
   @Column(name = "is_class_president", nullable = false)
   private boolean classPresident = false;
}