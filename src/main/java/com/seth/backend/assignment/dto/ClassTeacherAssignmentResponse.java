package com.seth.backend.assignment.dto;

public record ClassTeacherAssignmentResponse(
        Long id,
        Long schoolClassId,
        Long teacherId,
        String teacherName,
        Long subjectId,
        String subjectName,
        boolean homeroom
) {}