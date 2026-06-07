package com.gosqu.auth.dto.response;

public record AuthResponse(
        String token,
        String email,
        String name,
        String role
) {}
