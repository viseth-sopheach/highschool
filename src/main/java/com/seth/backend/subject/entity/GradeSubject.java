package com.seth.backend.subject.entity;

import com.seth.backend.academic.entity.Grade;
import com.seth.backend.academic.entity.StudyTrack;
import com.seth.backend.common.entity.IdentityEntity;
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
@Table(name = "grade_subjects")
public class GradeSubject extends IdentityEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "grade_id", nullable = false)
   private Grade grade;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "study_track_id")
   private StudyTrack studyTrack;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "subject_id", nullable = false)
   private Subject subject;
}