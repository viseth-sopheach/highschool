package com.seth.backend.assessment.mapper;

import com.seth.backend.assessment.dto.ScoreResponse;
import com.seth.backend.assessment.entity.Score;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoreMapper {

   @Mapping(target = "assessmentId", source = "assessment.id")
   @Mapping(target = "assessmentTitle", source = "assessment.title")
   @Mapping(target = "studentEnrollmentId", source = "studentEnrollment.id")
   @Mapping(target = "studentCode", source = "studentEnrollment.student.studentCode")
   @Mapping(target = "maxScore", source = "assessment.maxScore")
   @Mapping(target = "letterGrade", ignore = true)
   ScoreResponse toResponse(Score score);
}