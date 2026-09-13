package com.seth.backend.user.mapper;

import com.seth.backend.user.dto.UserResponse;
import com.seth.backend.user.entity.Role;
import com.seth.backend.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

   @Mapping(target = "roles", expression = "java(rolesToNames(user.getRoles()))")
   UserResponse toResponse(User user);

   default Set<String> rolesToNames(Set<Role> roles) {
      return roles.stream().map(Role::getName).collect(Collectors.toCollection(TreeSet::new));
   }
}