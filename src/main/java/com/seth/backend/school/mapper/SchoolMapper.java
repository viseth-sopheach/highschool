package com.seth.backend.school.mapper;

import com.seth.backend.school.dto.SchoolResponse;
import com.seth.backend.school.entity.School;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
   SchoolResponse toResponse(School school);
}