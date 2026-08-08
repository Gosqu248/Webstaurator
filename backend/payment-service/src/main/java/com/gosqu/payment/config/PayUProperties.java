package com.gosqu.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payu")
public record PayUProperties(
        String posId,
        String secondKey,
        String clientId,
        String clientSecret,
        String apiBaseUrl) {
}
