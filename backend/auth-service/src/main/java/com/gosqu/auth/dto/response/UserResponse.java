package com.gosqu.auth.dto.response;

public record UserResponse(
        Long id,
        String email,
        String name,
        String role
) {}
