package com.seth.backend.assignment.mapper;

import com.seth.backend.assignment.dto.ClassTeacherAssignmentResponse;
import com.seth.backend.assignment.entity.ClassTeacherAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassTeacherAssignmentMapper {

   @Mapping(target = "schoolClassId", source = "schoolClass.id")
   @Mapping(target = "teacherId", source = "teacher.id")
   @Mapping(target = "teacherName", source = "teacher.khmerName")
   @Mapping(target = "subjectId", source = "subject.id")
   @Mapping(target = "subjectName", source = "subject.nameEn")
   ClassTeacherAssignmentResponse toResponse(ClassTeacherAssignment assignment);
}