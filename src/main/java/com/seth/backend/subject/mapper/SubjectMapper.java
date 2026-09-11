package com.seth.backend.subject.mapper;

import com.seth.backend.subject.dto.SubjectResponse;
import com.seth.backend.subject.entity.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
   SubjectResponse toResponse(Subject subject);
}