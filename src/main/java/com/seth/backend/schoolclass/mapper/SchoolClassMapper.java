package com.seth.backend.schoolclass.mapper;

import com.seth.backend.schoolclass.dto.SchoolClassResponse;
import com.seth.backend.schoolclass.entity.SchoolClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolClassMapper {

   @Mapping(target = "academicYearId", source = "academicYear.id")
   @Mapping(target = "academicYearName", source = "academicYear.name")
   @Mapping(target = "gradeId", source = "grade.id")
   @Mapping(target = "gradeName", source = "grade.name")
   @Mapping(target = "studyTrackId", source = "studyTrack.id")
   @Mapping(target = "studyTrackName", source = "studyTrack.nameEn")
   SchoolClassResponse toResponse(SchoolClass schoolClass);
}