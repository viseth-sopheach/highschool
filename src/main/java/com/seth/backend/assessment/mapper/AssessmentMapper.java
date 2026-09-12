package com.seth.backend.assessment.mapper;

import com.seth.backend.assessment.dto.AssessmentResponse;
import com.seth.backend.assessment.entity.Assessment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssessmentMapper {

   @Mapping(target = "schoolClassId", source = "schoolClass.id")
   @Mapping(target = "schoolClassName", source = "schoolClass.name")
   @Mapping(target = "subjectId", source = "subject.id")
   @Mapping(target = "subjectName", source = "subject.nameEn")
   @Mapping(target = "assessmentTypeId", source = "assessmentType.id")
   @Mapping(target = "assessmentTypeName", source = "assessmentType.name")
   AssessmentResponse toResponse(Assessment assessment);
}