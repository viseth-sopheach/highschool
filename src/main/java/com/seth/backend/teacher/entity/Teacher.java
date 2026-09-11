package com.seth.backend.teacher.entity;

import com.seth.backend.common.entity.BaseEntity;
import com.seth.backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "teachers")
public class Teacher extends BaseEntity {

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "user_id", nullable = false, unique = true)
   private User user;

   @Column(name = "teacher_code", nullable = false, unique = true, length = 20)
   private String teacherCode;

   @Column(name = "khmer_name", nullable = false, length = 150)
   private String khmerName;

   @Column(name = "english_name", length = 150)
   private String englishName;

   @Column(length = 20)
   private String phone;

   @Column(name = "hire_date")
   private LocalDate hireDate;
}