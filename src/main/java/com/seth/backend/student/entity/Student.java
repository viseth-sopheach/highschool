package com.seth.backend.student.entity;

import com.seth.backend.common.entity.BaseEntity;
import com.seth.backend.school.entity.School;
import com.seth.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Filter(name = "schoolFilter", condition = "school_id = :schoolId")
@Table(name = "students")
public class Student extends BaseEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "school_id", nullable = false)
   private School school;

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "user_id", nullable = false, unique = true)
   private User user;

   @Column(name = "student_code", nullable = false, length = 20)
   private String studentCode;

   @Column(name = "khmer_name", nullable = false, length = 150)
   private String khmerName;

   @Column(name = "english_name", length = 150)
   private String englishName;

   private LocalDate dob;

   @Column(length = 10)
   private String gender;

   @Column(length = 20)
   private String phone;
}