package com.gosqu.auth.refreshToken;


public record RefreshTokenResult(
        RefreshToken entity,
        String rawToken
) {}
