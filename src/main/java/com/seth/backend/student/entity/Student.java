package com.seth.backend.student.entity;

import com.seth.backend.common.entity.BaseEntity;
import com.seth.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student extends BaseEntity {

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "user_id", nullable = false, unique = true)
   private User user;

   @Column(name = "student_code", nullable = false, unique = true, length = 20)
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