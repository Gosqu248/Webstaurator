package com.gosqu.gateway.filter;

import com.gosqu.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtil);
        lenient().when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("public path /auth/login passes through without JWT")
    void publicPath_authLogin_passesThrough() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/login").build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(any());
    }

    @Test
    @DisplayName("public path /oauth2/authorization passes through without JWT")
    void publicPath_oauth2_passesThrough() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/oauth2/authorization/google").build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(any());
    }

    @Test
    @DisplayName("protected path without Authorization header returns 401")
    void protectedPath_missingToken_returns401() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/users/me").build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("Authorization header without Bearer prefix return 401")
    void malformedBearer_returns401() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/order/1")
                        .header("Authorization", "Basic dXNlcjpwYXNz")
                        .build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("invalid JWT returns 401")
    void invalidToken_returns401() {
        when(jwtUtil.extractClaims("bad.token")).thenThrow(new JwtException("invalid"));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/users/me")
                        .header("Authorization", "Bearer bad.token")
                        .build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("valid JWT injects X-User-* headers into downstream request")
    void validToken_injectsUserHeaders() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("gosqu@gosqu.com");
        when(claims.get("role", String.class)).thenReturn("ADMIN");
        when(claims.get("userId", Long.class)).thenReturn(777L);
        when(jwtUtil.extractClaims("valid.token")).thenReturn(claims);

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        when(chain.filter(captor.capture())).thenReturn(Mono.empty());

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/users/me")
                        .header("Authorization", "Bearer valid.token")
                        .build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        var headers = captor.getValue().getRequest().getHeaders();
        assertThat(headers.getFirst("X-User-Email")).isEqualTo("gosqu@gosqu.com");
        assertThat(headers.getFirst("X-User-Role")).isEqualTo("ADMIN");
        assertThat(headers.getFirst("X-User-Id")).isEqualTo("777");
    }

    @Test
    @DisplayName("client-injected trust headers are stripped")
    void clientInjectedTrustHeaders_areStripped() {
        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        when(chain.filter(captor.capture())).thenReturn(Mono.empty());

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/login")
                        .header("X-User-Id", "999")
                        .header("X-User-Email", "hacker@evil.com")
                        .header("X-User-Role", "ADMIN")
                        .header("X-Internal-Service", "true")
                        .build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        var headers = captor.getValue().getRequest().getHeaders();
        assertThat(headers.getFirst("X-User-Id")).isNull();
        assertThat(headers.getFirst("X-User-Email")).isNull();
        assertThat(headers.getFirst("X-User-Role")).isNull();
        assertThat(headers.getFirst("X-Internal-Service")).isNull();
    }

    @Test
    @DisplayName("filter order is -1")
    void filterOrder_isMinusOne() {
        assertThat(filter.getOrder()).isEqualTo(-1);
    }

}