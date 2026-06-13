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

    @Value("${service.user-url}")
    private String userServiceUrl;

    @Value("${service.restaurant-url}")
    private String restaurantServiceUrl;

    @Value("${service.order-url}")
    private String orderServiceUrl;

    @Value("${service.payment-url}")
    private String paymentServiceUrl;

    @Value("${service.notification-url}")
    private String notificationsServiceUrl;

    @Value("${service.delivery-url}")
    private String deliveryServiceUrl;

    @Value("${service.review-url}")
    private String reviewServiceUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/auth/**", "/login/oauth2/**", "/oauth2/authorization/**")
                        .uri(authServiceUrl)
                )
                .route("user-service", r -> r
                        .path("/users/**")
                        .uri(userServiceUrl)
                )
                .route("restaurant-service", r -> r
                        .path("/restaurants/**")
                        .uri(restaurantServiceUrl)
                )
                .route("order-service", r -> r
                        .path("/orders/**")
                        .uri(orderServiceUrl)
                )
                .route("payment-service", r -> r
                        .path("/payments/**")
                        .uri(paymentServiceUrl)
                )
                .route("notification-service", r -> r
                        .path("/notifications/**")
                        .uri(notificationsServiceUrl)
                )
                .route("delivery-service", r -> r
                        .path("/deliveries/**")
                        .uri(deliveryServiceUrl)
                )
                .route("review-service", r -> r
                        .path("/reviews/**")
                        .uri(reviewServiceUrl)
                )
                .build();
    }
}
