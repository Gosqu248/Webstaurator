package com.gosqu.auth.jwt;

import com.gosqu.auth.config.AuthProperties;
import com.gosqu.auth.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final AuthProperties authProperties;

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String email, Role role, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        if (role != null) {
            claims.put("role", role.name());
        }
        return buildToken(claims, email, authProperties.accessTokenTtl());
    }

    public boolean isTokenValid(String token, String subject) {
        try {
            final String tokenSubject = extractSubject(token);
            return (tokenSubject.equals(subject) && !isTokenExpired(token));
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String buildToken(Map<String, Object> extraClaims, String email, long ttlMillis) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .id(UUID.randomUUID().toString())
                .subject(email)
                .claims(extraClaims)
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + ttlMillis))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("JWT token unsupported: {}", e.getMessage());
            throw new RuntimeException("JWT token is unsupported", e);
        } catch (MalformedJwtException e) {
            log.error("JWT token malformed: {}", e.getMessage());
            throw new RuntimeException("JWT token is malformed", e);
        } catch (SignatureException e) {
            log.error("JWT signature invalid: {}", e.getMessage());
            throw new RuntimeException("JWT signature validation failed", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims empty: {}", e.getMessage());
            throw new RuntimeException("JWT claims string is empty", e);
        }
    }
}
