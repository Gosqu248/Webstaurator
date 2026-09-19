package com.gosqu.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        // PayU IPN: public because PayU signs the body (MD5 verified in service layer)
                        .requestMatchers("/payments/notify").permitAll()
                        // Remaining endpoints: gateway validates JWT and injects X-User-* headers;
                        // this service is not exposed directly — network policy enforced in k8s.
                        // Ownership checks are enforced at service layer for IDOR protection.
                        .anyRequest().permitAll()
                )
                .build();
    }
}
