package com.gosqu.notificationservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
public class UserClient {

    private static final String INTERNAL_HEADER = "X-Internal-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    UserClient(@Value("${services.auth-service.url}") String baseUrl,
               @Value("${internal.api-key}") String internalApiKey) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.internalApiKey = internalApiKey;
    }

    public String getEmail(UUID userId) {
        log.debug("action=get_user_email userId={}", userId);
        InternalEmailResponse response = restClient.get()
                .uri("/internal/users/{userId}/email", userId)
                .header(INTERNAL_HEADER, internalApiKey)
                .retrieve()
                .body(InternalEmailResponse.class);
        return response != null ? response.email() : null;
    }

    record InternalEmailResponse(String email) {}
}
