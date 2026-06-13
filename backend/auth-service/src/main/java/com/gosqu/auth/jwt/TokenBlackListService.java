package com.gosqu.auth.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlackListService {

    private static final String BLACK_LIST_PREFIX = "blacklist:token";

    private final StringRedisTemplate redisTemplate;

    public void blackList(String jti, Duration remainingTtl) {
        if (remainingTtl.isNegative() || remainingTtl.isZero()) {
            return;
        }

        String key = BLACK_LIST_PREFIX + ":" + jti;
        redisTemplate.opsForValue().set(key, "1", remainingTtl);
        log.debug("Token blacklisted: jti={}, ttl={}", jti, remainingTtl);
    }

    public boolean isBlackListed(String jti) {
        String key = BLACK_LIST_PREFIX + ":" + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
