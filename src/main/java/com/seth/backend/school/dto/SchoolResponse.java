package com.seth.backend.school.dto;

public record SchoolResponse(
        Long id,
        String code,
        String name,
        String address,
        String phone,
        boolean active
) {}