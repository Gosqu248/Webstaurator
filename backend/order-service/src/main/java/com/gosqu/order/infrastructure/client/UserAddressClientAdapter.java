package com.gosqu.order.infrastructure.client;

import com.gosqu.order.application.port.out.UserAddressClientPort;
import com.gosqu.order.domain.exception.UserServiceUnavailableException;
import com.gosqu.order.infrastructure.client.dto.AddressSnapshot;
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
public class UserAddressClientAdapter implements UserAddressClientPort {

    private static final String INTERNAL_HEADER = "X-Internal-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    UserAddressClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${services.user-service.url}") String baseUrl,
            @Value("${internal.api-key}") String internalApiKey) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.internalApiKey = internalApiKey;
    }

    @Override
    @CircuitBreaker(name = "user-service")
    @Bulkhead(name = "user-service")
    @Retry(name = "user-service", fallbackMethod = "getDefaultAddressFallback")
    public AddressSnapshot getDefaultAddress(UUID userId) {
        log.debug("action=get_default_address userId={}", userId);
        return restClient.get().uri("/internal/users/{userId}/addresses/default", userId)
                .header(INTERNAL_HEADER, internalApiKey).retrieve().body(AddressSnapshot.class);
    }

    private AddressSnapshot getDefaultAddressFallback(UUID userId, HttpClientErrorException ex) {
        throw ex;
    }

    private AddressSnapshot getDefaultAddressFallback(UUID userId, Throwable ex) {
        log.warn("action=user_service_unavailable userId={} cause={}", userId, ex.toString());
        throw new UserServiceUnavailableException(userId, ex);
    }
}
