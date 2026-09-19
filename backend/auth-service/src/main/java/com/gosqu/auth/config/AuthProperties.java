package com.gosqu.auth.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "auth")
@Validated
public record AuthProperties(
        @Positive long accessTokenTtl,
        @Positive long refreshTokenTtl,
        Cookie cookie,
        @DefaultValue("http://localhost:4200") String frontendUrl,
        @DefaultValue("24") @Positive int passwordResetTokenTtlHours
) {
    public AuthProperties {
        if (accessTokenTtl <= 0) {
            accessTokenTtl = 24 * 60 * 60 * 1000L; // 24 hours
        }
        if (refreshTokenTtl <= 0) {
            refreshTokenTtl = 7 * 24 * 60 * 60 * 1000L; // 7 days
        }
        if (cookie == null) {
            cookie = new Cookie(true, "/api/v1/auth", "refreshToken");
        }
        if (frontendUrl == null || frontendUrl.isBlank()) {
            frontendUrl = "http://localhost:4200";
        }
    }

    public record Cookie(
            boolean secure,
            @NotBlank String path,
            @NotBlank String name
    ) {
        public Cookie {
            if (path == null || path.isBlank()) {
                path = "/api/v1/auth";
            }
            if (name == null || name.isBlank()) {
                name = "refreshToken";
            }
        }
    }

    public int getRefreshTokenTtlSeconds() {
        return (int) (refreshTokenTtl / 1000);
    }
}
