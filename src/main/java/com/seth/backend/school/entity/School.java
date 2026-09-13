package com.seth.backend.school.entity;

import com.seth.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "schools")
public class School extends BaseEntity {

   @Column(nullable = false, unique = true, length = 20)
   private String code;

   @Column(nullable = false, length = 150)
   private String name;

   @Column(length = 255)
   private String address;

   @Column(length = 20)
   private String phone;

   @Column(name = "is_active", nullable = false)
   private boolean active = true;
}