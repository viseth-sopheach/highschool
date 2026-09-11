package com.seth.backend.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import com.seth.backend.common.entity.CreatedOnlyEntity;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission extends CreatedOnlyEntity {

   @Column(nullable = false, unique = true, length = 100)
   private String name;

   @Column(length = 255)
   private String description;
}