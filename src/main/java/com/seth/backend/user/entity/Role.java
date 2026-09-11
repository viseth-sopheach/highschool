package com.seth.backend.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.seth.backend.common.entity.CreatedOnlyEntity;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends CreatedOnlyEntity {

   @Column(nullable = false, unique = true, length = 50)
   private String name;

   @Column(length = 255)
   private String description;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name = "role_permissions",
           joinColumns = @JoinColumn(name = "role_id"),
           inverseJoinColumns = @JoinColumn(name = "permission_id")
   )
   private Set<Permission> permissions = new HashSet<>();
}