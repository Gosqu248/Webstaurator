package pl.urban.backend.tests.unit;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.urban.backend.enums.Role;
import pl.urban.backend.model.User;
import pl.urban.backend.security.JwtUtil;



import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JwtUtilTests {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtilTests.class);

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.rotateKey();
        jwtUtil.expiration = 3600L;

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.user);
    }

    @Test
    void testGenerateToken() {
        logger.info("Running testGenerateToken");
        String token = jwtUtil.generateToken(testUser);

        assertNotNull(token, "Token should not be null");
        assertEquals(3, token.split("\\.").length, "Token should be in valid JWT format");
        logger.info("Completed testGenerateToken");
    }

    @Test
    void testExtractSubjectFromToken() {
        logger.info("Running testExtractSubjectFromToken");
        String token = jwtUtil.generateToken(testUser);
        String subject = jwtUtil.extractSubjectFromToken(token);

        assertEquals(testUser.getEmail(), subject, "Subject should match the user's email");
        logger.info("Completed testExtractSubjectFromToken");
    }

    @Test
    void testExtractClaims() {
        logger.info("Running testExtractClaims");
        String token = jwtUtil.generateToken(testUser);
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims, "Claims should not be null");
        assertEquals(testUser.getEmail(), claims.getSubject(), "Claims subject should match user's email");
        assertEquals("user", claims.get("role"), "Claims should contain correct user role");
        logger.info("Completed testExtractClaims");
    }

    @Test
    void testRotateKeyInvalidatesOldTokens() {
        logger.info("Running testRotateKeyInvalidatesOldTokens");
        String token = jwtUtil.generateToken(testUser);
        jwtUtil.rotateKey(); // Rotate key to invalidate previous tokens

        Exception exception = assertThrows(io.jsonwebtoken.security.SecurityException.class, () -> {
            jwtUtil.extractSubjectFromToken(token);
        });

        assertTrue(exception.getMessage().contains("JWT signature does not match"), "Old token should be invalid");
        logger.info("Completed testRotateKeyInvalidatesOldTokens");
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        logger.info("Running testTokenExpiration");
        jwtUtil.expiration = 1L; // Set short expiration time for testing
        String token = jwtUtil.generateToken(testUser);

        Thread.sleep(2000); // Wait for the token to expire

        Exception exception = assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
            jwtUtil.extractSubjectFromToken(token);
        });

        assertTrue(exception.getMessage().contains("JWT expired"), "Expired token should throw exception");
        logger.info("Completed testTokenExpiration");
    }

}
