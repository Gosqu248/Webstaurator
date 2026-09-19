package com.gosqu.auth.service;

public record LoginResult(
        String accessToken,
        String rawRefreshToken,
        String email,
        String name,
        String role
) {}
