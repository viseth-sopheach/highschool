package com.seth.backend.teacher.mapper;

import com.seth.backend.teacher.dto.TeacherResponse;
import com.seth.backend.teacher.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

   @Mapping(target = "userId", source = "user.id")
   @Mapping(target = "username", source = "user.username")
   TeacherResponse toResponse(Teacher teacher);
}