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
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("auth-service-cb")
                                .setFallbackUri("forward:/fallback/auth-service")
                        ))
                        .uri(authServiceUrl)
                )
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("user-service-cb")
                                .setFallbackUri("forward:/fallback/user-service")
                        ))
                        .uri(userServiceUrl)
                )
                .route("restaurant-service", r -> r
                        .path("/restaurants/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("restaurant-service-cb")
                                .setFallbackUri("forward:/fallback/restaurant-service")
                        ))
                        .uri(restaurantServiceUrl)
                )
                .route("order-service", r -> r
                        .path("/orders/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("order-service-cb")
                                .setFallbackUri("forward:/fallback/order-service")
                        ))
                        .uri(orderServiceUrl)
                )
                .route("payment-service", r -> r
                        .path("/payments/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("payment-service-cb")
                                .setFallbackUri("forward:/fallback/payment-service")
                        ))
                        .uri(paymentServiceUrl)
                )
                .route("notification-service", r -> r
                        .path("/notifications/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("notification-service-cb")
                                .setFallbackUri("forward:/fallback/notification-service")
                        ))
                        .uri(notificationsServiceUrl)
                )
                .route("delivery-service", r -> r
                        .path("/deliveries/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("delivery-service-cb")
                                .setFallbackUri("forward:/fallback/delivery-service")
                        ))
                        .uri(deliveryServiceUrl)
                )
                .route("review-service", r -> r
                        .path("/reviews/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("review-service-cb")
                                .setFallbackUri("forward:/fallback/review-service")
                        ))
                        .uri(reviewServiceUrl)
                )
                .build();
    }
}
