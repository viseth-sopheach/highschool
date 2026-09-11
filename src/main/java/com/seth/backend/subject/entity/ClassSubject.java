package com.seth.backend.subject.entity;

import com.seth.backend.common.entity.IdentityEntity;
import com.seth.backend.schoolclass.entity.SchoolClass;
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
@Table(name = "class_subjects")
public class ClassSubject extends IdentityEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_class_id", nullable = false)
   private SchoolClass schoolClass;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "subject_id", nullable = false)
   private Subject subject;
}