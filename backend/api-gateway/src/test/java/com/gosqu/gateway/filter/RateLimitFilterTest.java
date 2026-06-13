package com.gosqu.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOps;

    @Mock
    private GatewayFilterChain chain;

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter(redisTemplate);
        ReflectionTestUtils.setField(filter, "publicMax", 3);
        ReflectionTestUtils.setField(filter, "authMax", 10);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(redisTemplate.expire(any(), any())).thenReturn(Mono.just(true));
        lenient().when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("request under limit passes through")
    void belowLimit_passesThrough() {
        when(valueOps.increment(any())).thenReturn(Mono.just(1L));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/restaurants").build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(any());
    }

    @Test
    @DisplayName("request over public limit return 429")
    void abovePublicLimit_returns429() {
        when(valueOps.increment(any())).thenReturn(Mono.just(4L));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/restaurants").build());

        StepVerifier.create(filter.filter(exchange,chain))
                .verifyComplete();

        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("request over auth limit return 429")
    void aboveAuthLimit_returns429() {
        when(valueOps.increment(any())).thenReturn(Mono.just(11L));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/restaurants").build());

        StepVerifier.create(filter.filter(exchange,chain))
                .verifyComplete();

        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("authenticated user uses per-user key in Redis")
    void authenticatedRequest_usesUserKey() {
        when(valueOps.increment(argThat(key -> key.contains("user:42")))).thenReturn(Mono.just(1L));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/restaurants")
                        .header("X-User-Id", "42")
                        .build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(valueOps).increment(argThat(key -> key.startsWith("rate_limit:user:42:")));
    }

    @Test
    @DisplayName("unauthenticated user uses per-ip key in Redis")
    void unauthenticatedRequest_usesIpKey() {
        when(valueOps.increment(argThat(key -> key.startsWith("rate_limit:ip:")))).thenReturn(Mono.just(1L));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/login").build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(valueOps).increment(argThat(key -> key.startsWith("rate_limit:ip:")));
    }

    @Test
    @DisplayName("X-Forwarded-For used as client IP")
    void xForwardedFor_usedAsClientIp() {
        when(valueOps.increment(argThat(key -> key.contains("10.0.0.5")))).thenReturn(Mono.just(1L));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/login")
                        .header("X-Forwarded-For", "10.0.0.5, 172.16.0.1")
                        .build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(valueOps).increment(argThat(key -> key.contains("10.0.0.5")));
    }

    @Test
    @DisplayName("First request sets expire on key")
    void firstRequest_setsExpireOnKey() {
        when(valueOps.increment(any())).thenReturn(Mono.just(1L));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/restaurants").build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(redisTemplate).expire(any(), any());
    }

    @Test
    @DisplayName("Subsequent request does not reset expire")
    void subsequentRequest_doesNotResetExpire() {
        when(valueOps.increment(any())).thenReturn(Mono.just(2L));

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/restaurants").build());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(redisTemplate, never()).expire(any(), any());
    }

    @Test
    @DisplayName("filter order is zero (after JwtAuthenticationFilter)")
    void filterOrder_isZero() {
        assertThat(filter.getOrder()).isEqualTo(0);
    }
}
