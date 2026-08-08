package com.gosqu.order.infrastructure.client;

import com.gosqu.order.application.port.out.UserAddressClientPort;
import com.gosqu.order.infrastructure.client.dto.AddressSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
public class UserAddressClientAdapter implements UserAddressClientPort {

    private static final String INTERNAL_HEADER = "X-Internal-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    UserAddressClientAdapter(
            @Value("${services.user-service.url}") String baseUrl,
            @Value("${internal.api-key}") String internalApiKey) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.internalApiKey = internalApiKey;
    }

    @Override
    public AddressSnapshot getDefaultAddress(UUID userId) {
        log.debug("action=get_default_address userId={}", userId);
        return restClient.get()
                .uri("/internal/users/{userId}/addresses/default", userId)
                .header(INTERNAL_HEADER, internalApiKey)
                .retrieve()
                .body(AddressSnapshot.class);
    }
}
