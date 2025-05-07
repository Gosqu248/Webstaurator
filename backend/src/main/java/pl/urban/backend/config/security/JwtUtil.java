package pl.urban.backend.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.urban.backend.model.User;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.expiration}")
    public Long standardExpirationSeconds;
    @Value("${jwt.shortExpiration}")
    private Long shortExpirationSeconds;

    @Value("${jwt.secret}")
    private String secretKeyString;
    private Key secretKey;
    private JwtParser jwtParser;
    private boolean initialized = false;

    @PostConstruct
    private void init() {
        if (initialized) return;

        if (secretKeyString == null || secretKeyString.trim().isEmpty()) {
            logger.warn("No JWT secret provided; generating a random key (test‐mode).");
            secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            try {
                byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
                secretKey = Keys.hmacShaKeyFor(keyBytes);
            } catch (Exception e) {
                logger.error("Failed to decode provided secret; falling back to random key.", e);
                secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }
        }

        jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
        initialized = true;
        logger.info("JWT key initialized.");
    }

    private void ensureInitialized() {
        if (!initialized) {
            init();
        }
    }

    private String generateToken(@NotNull User user, TokenPurpose purpose) {
        ensureInitialized();

        if (user.getEmail() == null) {
            throw new IllegalArgumentException("User or email cannot be null");
        }

        boolean isPasswordReset = TokenPurpose.PASSWORD_RESET.equals(purpose);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user");
        claims.put("purpose", purpose.getValue());

        long expirationTime = isPasswordReset ? shortExpirationSeconds : standardExpirationSeconds;
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationTime);

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getEmail())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            logger.error("Failed to generate JWT token", e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String generateAuthToken(User user) {
        return generateToken(user, TokenPurpose.AUTH);
    }

    public String generatePasswordResetToken(User user) {
        return generateToken(user, TokenPurpose.PASSWORD_RESET);
    }

    public String extractSubject(String token) {
        ensureInitialized();
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimsResolver) {
        if (token == null) {
            throw new IllegalArgumentException("Token and claims resolver cannot be null");
        }
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        ensureInitialized();
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            logger.error("Failed to parse JWT token", e);
            throw e;
        }
    }

    public boolean isTokenValid(String token, TokenPurpose expectedPurpose) {
        ensureInitialized();
        try {
            Claims claims = extractAllClaims(token);
            if (claims.getExpiration().before(Date.from(Instant.now()))) {
                throw new JwtException("Token is expired");
            }
            String purpose = claims.get("purpose", String.class);
            if (!expectedPurpose.getValue().equals(purpose)) {
                throw new JwtException("Token purpose does not match");
            }
            return true;
        } catch (Exception e) {
            throw new JwtException("Token is invalid", e);
        }
    }
}
