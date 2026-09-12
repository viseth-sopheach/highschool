package com.seth.backend.assignment.entity;

import com.seth.backend.common.entity.CreatedOnlyEntity;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.subject.entity.Subject;
import com.seth.backend.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "class_teacher_assignments")
public class ClassTeacherAssignment extends CreatedOnlyEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_class_id", nullable = false)
   private SchoolClass schoolClass;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "teacher_id", nullable = false)
   private Teacher teacher;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "subject_id")
   private Subject subject;

   @Column(name = "is_homeroom", nullable = false)
   private boolean homeroom = false;
}