package pl.urban.backend.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
    public Long standardExpirationSeconds; //długość ważności tokenu
    @Value("${jwt.shortExpiration}")
    private Long shortExpirationSeconds; //długość ważności tokenu

    @Value("${jwt.secret}")
    private String secretKeyString; //klucz do podpisywania tokenu
    private Key secretKey;
    private JwtParser jwtParser;


    @PostConstruct
    private void init() {
        if (secretKeyString == null || secretKeyString.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not set");
        }

        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
            secretKey = Keys.hmacShaKeyFor(keyBytes);
            jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
            logger.info("JWT secret key initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize JWT secret key", e);
            throw new IllegalStateException("Failed to initialize JWT secret key", e);
        }
    }

    private String generateToken(@org.jetbrains.annotations.NotNull User user, TokenPurpose purpose) {
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
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            logger.error("Failed to parse JWT token", e);
            throw e;
        }
    }

    public boolean isTokenValid(String token, TokenPurpose expectedPurpose) {
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
