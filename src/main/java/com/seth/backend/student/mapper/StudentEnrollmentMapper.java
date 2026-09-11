package com.seth.backend.student.mapper;

import com.seth.backend.student.dto.StudentEnrollmentResponse;
import com.seth.backend.student.entity.StudentEnrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentEnrollmentMapper {

   @Mapping(target = "studentId", source = "student.id")
   @Mapping(target = "studentCode", source = "student.studentCode")
   @Mapping(target = "schoolClassId", source = "schoolClass.id")
   @Mapping(target = "schoolClassName", source = "schoolClass.name")
   @Mapping(target = "academicYearId", source = "schoolClass.academicYear.id")
   @Mapping(target = "academicYearName", source = "schoolClass.academicYear.name")
   StudentEnrollmentResponse toResponse(StudentEnrollment enrollment);
}