package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.urban.backend.controller.AuthController;
import pl.urban.backend.model.User;
import pl.urban.backend.dto.response.JwtResponse;
import pl.urban.backend.dto.request.UserRequest;
import pl.urban.backend.dto.request.TwoFactorVerificationRequest;
import pl.urban.backend.config.security.JwtUtil;
import pl.urban.backend.service.UserSecurityService;
import pl.urban.backend.service.UserService;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AuthTests {
    private static final Logger logger = LoggerFactory.getLogger(AuthTests.class);

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserSecurityService userSecurityService;

    @Spy
    private BCryptPasswordEncoder brcyptPasswordEncoder;
    private User testUser;

    private final UserRequest testUserRequest = new UserRequest(
            "test@example.com",
            "password"
    );

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword(brcyptPasswordEncoder.encode("password"));
    }

    @Test
    void testRegisterUser() {
        logger.info("Running testRegisterUser");
        doNothing().when(userService).registerUser(any(UserRequest.class));



        ResponseEntity<?> response = authController.registerUser(testUserRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) Objects.requireNonNull(response.getBody())).containsKey("message"));
        assertEquals("User registered successfully", ((Map<?, ?>) response.getBody()).get("message"));
        verify(userService, times(1)).registerUser(eq(testUserRequest));
        logger.info("Completed testRegisterUser");
    }

    @Test
    void testLoginUserSuccess() throws Exception {
        logger.info("Running testLoginUserSuccess");
        UserRequest userRequest = new UserRequest(
                "test@example.com",
                "password"
        );

        when(userService.getUserBySubject(eq(userRequest.email()))).thenReturn(testUser);
        when(userSecurityService.isAccountLocked(eq(testUser))).thenReturn(false);
        doAnswer(invocation -> null).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        doAnswer(invocation -> null).when(userSecurityService).resetFailedLoginAttempts(eq(testUser));
        doAnswer(invocation -> null).when(userSecurityService).generateAndSendTwoFactorCode(eq(testUser));

        ResponseEntity<?> response = authController.loginUser(userRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody());
        logger.info("Completed testLoginUserSuccess");
    }

    @Test
    void testLoginUserAccountLocked() throws Exception {
        logger.info("Running testLoginUserAccountLocked");
        UserRequest userRequest = new UserRequest(
                "test@example.com",
                "password"
        );

        when(userService.getUserBySubject(eq(userRequest.email()))).thenReturn(testUser);
        when(userSecurityService.isAccountLocked(eq(testUser))).thenReturn(true);

        ResponseEntity<?> response = authController.loginUser(userRequest);

        assertEquals(423, response.getStatusCodeValue());
        assertEquals("Account is locked. Try again later.", response.getBody());
        logger.info("Completed testLoginUserAccountLocked");
    }

    @Test
    void testVerifyTwoFactorCodeSuccess() throws Exception {
        logger.info("Running testVerifyTwoFactorCodeSuccess");
        TwoFactorVerificationRequest request = new TwoFactorVerificationRequest(
                "test@example.com",
                "123456"
        );

        when(userService.getUserBySubject(eq(request.email()))).thenReturn(testUser);
        when(userSecurityService.verifyTwoFactorCode(eq(testUser), eq(request.code()))).thenReturn(true);
        when(jwtUtil.generateAuthToken(eq(testUser))).thenReturn("test-token");

        ResponseEntity<?> response = authController.verifyTwoFactorCode(request);

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(JwtResponse.class, response.getBody());
        logger.info("Completed testVerifyTwoFactorCodeSuccess");
    }

    @Test
    void testVerifyTwoFactorCodeFailure() {
        logger.info("Running testVerifyTwoFactorCodeFailure");
        TwoFactorVerificationRequest request = new TwoFactorVerificationRequest(
                "test@example.com",
                "wrong-code"
        );

        when(userService.getUserBySubject(eq(request.email()))).thenReturn(testUser);
        when(userSecurityService.verifyTwoFactorCode(eq(testUser), eq(request.code()))).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> {
            authController.verifyTwoFactorCode(request);
        });

        assertEquals("Bad verification codes", exception.getMessage());
        logger.info("Completed testVerifyTwoFactorCodeFailure");
    }

    @Test
    void testRegisterUserFailure() {
        logger.info("Running testRegisterUserFailure");
        doThrow(new RuntimeException("Registration failed")).when(userService).registerUser(any(UserRequest.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.registerUser(testUserRequest);
        });

        assertEquals("Registration failed", exception.getMessage());
        verify(userService, times(1)).registerUser(eq(testUserRequest));
        logger.info("Completed testRegisterUserFailure");
    }

    @Test
    void testLoginUserIncorrectPassword() {
        logger.info("Running testLoginUserIncorrectPassword");
        UserRequest userRequest = new UserRequest(
                "test@example.com",
                "wrong-password"
        );

        when(userService.getUserBySubject(eq(userRequest.email()))).thenReturn(testUser);
        when(userSecurityService.isAccountLocked(eq(testUser))).thenReturn(false);
        // Simulate bad credentials inside authenticate():
        doThrow(new RuntimeException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<?> response = authController.loginUser(userRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
        logger.info("Completed testLoginUserIncorrectPassword");
    }



}
