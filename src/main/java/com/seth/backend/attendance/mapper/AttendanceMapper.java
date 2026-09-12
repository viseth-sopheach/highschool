package com.seth.backend.attendance.mapper;

import com.seth.backend.attendance.dto.AttendanceResponse;
import com.seth.backend.attendance.entity.AttendanceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

   @Mapping(target = "studentId", source = "student.id")
   @Mapping(target = "studentName", source = "student.khmerName")
   @Mapping(target = "schoolClassId", source = "schoolClass.id")
   @Mapping(target = "schoolClassName", source = "schoolClass.name")
   AttendanceResponse toResponse(AttendanceRecord record);
}