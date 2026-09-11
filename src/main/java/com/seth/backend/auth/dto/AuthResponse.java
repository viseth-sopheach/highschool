package com.seth.backend.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresInMs
) {}