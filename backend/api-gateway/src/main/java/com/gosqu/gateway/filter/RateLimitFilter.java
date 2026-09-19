package com.gosqu.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${rate-limit.public.max:20}")
    private int publicMax;

    @Value("${rate-limit.auth.max:200}")
    private int authMax;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String key = buildKey(exchange, userId);
        int limit = userId != null ? authMax : publicMax;

        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        redisTemplate.expire(key, Duration.ofSeconds(60)).subscribe();
                    }
                    if (count > limit) {
                        return tooManyRequest(exchange);
                    }
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private String buildKey(ServerWebExchange exchange, String userId) {
        long window = System.currentTimeMillis() / 60_000; //okno minutowe
        if (userId != null) {
            return "rate_limit:user:" + userId + ":" + window;
        }
        return "rate_limit:ip:" + getClientIp(exchange) + ":" + window;
    }

    private String getClientIp(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null) {
            return forwarded.split(",")[0].trim();
        }
        var addr = exchange.getRequest().getRemoteAddress();
        return addr != null ? addr.getAddress().getHostAddress() : "unknown";
    }

    private Mono<Void> tooManyRequest(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().set("Retry-After", "60");
        return exchange.getResponse().setComplete();
    }
}
