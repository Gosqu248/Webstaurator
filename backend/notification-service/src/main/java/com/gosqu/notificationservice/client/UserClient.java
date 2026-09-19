package com.gosqu.notificationservice.client;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
public class UserClient {

    private static final String INTERNAL_HEADER = "X-Internal-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    UserClient(RestClient.Builder restClientBuilder,
               @Value("${services.auth-service.url}") String baseUrl,
               @Value("${internal.api-key}") String internalApiKey) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.internalApiKey = internalApiKey;
    }

    @CircuitBreaker(name = "auth-service")
    @Bulkhead(name = "auth-service")
    @Retry(name = "auth-service", fallbackMethod = "getEmailFallback")
    public String getEmail(UUID userId) {
        log.debug("action=get_user_email userId={}", userId);
        InternalEmailResponse response = restClient.get()
                .uri("/internal/users/{userId}/email", userId)
                .header(INTERNAL_HEADER, internalApiKey)
                .retrieve()
                .body(InternalEmailResponse.class);
        return response != null ? response.email() : null;
    }

    private String getEmailFallback(UUID userId, HttpClientErrorException ex) {
        throw ex;
    }

    private String getEmailFallback(UUID userId, Throwable ex) {
        log.warn("action=auth_service_unavailable userId={} cause={}", userId, ex.toString());
        throw new IllegalStateException("auth-service unavailable, cannot resolve email for userId=" + userId, ex);
    }

    record InternalEmailResponse(String email) {}
}
