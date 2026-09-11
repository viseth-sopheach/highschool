package com.seth.backend.subject.mapper;

import com.seth.backend.subject.dto.ClassSubjectResponse;
import com.seth.backend.subject.entity.ClassSubject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassSubjectMapper {

   @Mapping(target = "schoolClassId", source = "schoolClass.id")
   @Mapping(target = "subjectId", source = "subject.id")
   @Mapping(target = "subjectCode", source = "subject.code")
   @Mapping(target = "subjectNameEn", source = "subject.nameEn")
   ClassSubjectResponse toResponse(ClassSubject classSubject);
}