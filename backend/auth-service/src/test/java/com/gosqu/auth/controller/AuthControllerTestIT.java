package com.gosqu.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gosqu.auth.GosquSpringBootTest;
import com.gosqu.auth.dto.request.LoginRequest;
import com.gosqu.auth.dto.request.RegisterRequest;
import com.gosqu.auth.dto.request.TwoFactorRequest;
import com.gosqu.auth.dto.response.AuthResponse;
import com.gosqu.auth.refreshToken.RefreshTokenRepository;
import com.gosqu.auth.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@GosquSpringBootTest
class AuthControllerTestIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    JavaMailSender mailSender;

    private static final String EMAIL = "integration@example.com";
    private static final String NAME = "Integration User";
    private static final String PASSWORD = "password123";

    @BeforeEach
    void cleanUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        redisTemplate.delete("2fa:" + EMAIL);
    }

    @Test
    @DisplayName("POST /auth/register with new user returns 201")
    void register_newUser_returns201() throws Exception {
        String body = objectMapper.writeValueAsString(new RegisterRequest(EMAIL, NAME, PASSWORD));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /auth/register with duplicate email returns 409")
    void register_duplicateEmail_returns409() throws Exception {
        String body = objectMapper.writeValueAsString(new RegisterRequest(EMAIL, NAME, PASSWORD));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Full login flow: register → login → verify-2fa → GET /auth/me")
    void fullLoginFlow_returnsJwtAndAllowsAccessToMe() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(EMAIL, NAME, PASSWORD))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, PASSWORD))))
                .andExpect(status().isOk());

        String code = redisTemplate.opsForValue().get("2fa:" + EMAIL);
        assertThat(code).isNotNull().hasSize(6);

        MvcResult verifyResult = mockMvc.perform(post("/auth/verify-2fa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TwoFactorRequest(EMAIL, code))))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                verifyResult.getResponse().getContentAsString(), AuthResponse.class);
        assertThat(authResponse.token()).isNotBlank();
        assertThat(authResponse.email()).isEqualTo(EMAIL);
        assertThat(authResponse.name()).isEqualTo(NAME);

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/login with wrong password returns 401")
    void login_wrongPassword_returns401() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(EMAIL, NAME, PASSWORD))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, "wrongpass123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/verify-2fa with wrong code returns 401")
    void verify2fa_wrongCode_returns401() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(EMAIL, NAME, PASSWORD))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, PASSWORD))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/verify-2fa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TwoFactorRequest(EMAIL, "000000"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /auth/me without token returns 401")
    void getMe_noToken_returns401() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
