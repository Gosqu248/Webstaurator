package com.gosqu.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Value("${service.auth-url}")
    private String authServiceUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/auth/**", "/login/oauth2/**", "/oauth2/authorization/**")
                        .uri(authServiceUrl)
                )
                // Future services will be added here:
                // .route("restaurant-service", r -> r.path("/restaurants/**").uri(restaurantServiceUrl))
                // .route("order-service", r -> r.path("/orders/**").uri(orderServiceUrl))
                .build();
    }
}
