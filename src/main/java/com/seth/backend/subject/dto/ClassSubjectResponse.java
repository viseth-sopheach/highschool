package com.seth.backend.subject.dto;

public record ClassSubjectResponse(
        Long id,
        Long schoolClassId,
        Long subjectId,
        String subjectCode,
        String subjectNameEn
) {}