package com.seth.backend.attendance.entity;

import com.seth.backend.common.entity.BaseEntity;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.student.entity.Student;
import com.seth.backend.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord extends BaseEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "student_id", nullable = false)
   private Student student;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_class_id", nullable = false)
   private SchoolClass schoolClass;

   @Column(name = "attendance_date", nullable = false)
   private LocalDate attendanceDate;

   @Enumerated(EnumType.STRING)
   @Column(nullable = false, length = 20)
   private AttendanceStatus status;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "recorded_by", nullable = false)
   private Teacher recordedBy;

   @Column(length = 255)
   private String remarks;
}