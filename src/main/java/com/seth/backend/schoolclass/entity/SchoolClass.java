package com.seth.backend.schoolclass.entity;

import com.seth.backend.academic.entity.AcademicYear;
import com.seth.backend.academic.entity.Grade;
import com.seth.backend.academic.entity.StudyTrack;
import com.seth.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "school_classes")
public class SchoolClass extends BaseEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "academic_year_id", nullable = false)
   private AcademicYear academicYear;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "grade_id", nullable = false)
   private Grade grade;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "study_track_id")
   private StudyTrack studyTrack;

   @Column(nullable = false, length = 10)
   private String name;

   private Short capacity;
}