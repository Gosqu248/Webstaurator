package com.gosqu.delivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// Autoryzacja jest już zrobiona na poziomie api-gateway (waliduje JWT i wstrzykuje X-User-Id/
// X-User-Role po weryfikacji) — tutaj tylko wyłączamy sesje/CSRF (serwis jest bezstanowy) i
// sprawdzamy rolę ręcznie w kontrolerze tam, gdzie jest to wymagane (DeliveryController.requireRole),
// dokładnie tak jak w review-service.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()
                )
                .build();
    }
}
