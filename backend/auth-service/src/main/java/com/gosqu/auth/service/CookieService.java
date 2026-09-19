package com.gosqu.auth.service;

import com.gosqu.auth.config.AuthProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CookieService {

    private final AuthProperties authProperties;

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(authProperties.cookie().name(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(authProperties.cookie().secure());
        cookie.setPath(authProperties.cookie().path());
        cookie.setMaxAge(authProperties.getRefreshTokenTtlSeconds());
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(authProperties.cookie().name(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(authProperties.cookie().secure());
        cookie.setPath(authProperties.cookie().path());
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> authProperties.cookie().name().equals(c.getName()))
                .map(Cookie::getValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
    }
}
