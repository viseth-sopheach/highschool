package com.seth.backend.student.mapper;

import com.seth.backend.student.dto.StudentResponse;
import com.seth.backend.student.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

   @Mapping(target = "userId", source = "user.id")
   @Mapping(target = "username", source = "user.username")
   StudentResponse toResponse(Student student);
}