package pl.urban.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.urban.backend.config.security.JwtTokenFilter;
import pl.urban.backend.service.GoogleAuthService;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig  {
    private final JwtTokenFilter jwtTokenFilter;
    private final GoogleAuthService googleAuthService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                        auth.requestMatchers("/api/auth/").authenticated();
                        auth.requestMatchers("/api/address/").authenticated();
                        auth.requestMatchers("/api/auth/google", "/api/auth/google/failure").permitAll();
                        auth.anyRequest().permitAll();
                        }
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2login -> {
                    oauth2login
                            .successHandler((request, response, authentication) ->{
                                OAuth2User principal = (OAuth2User) authentication.getPrincipal();
                                String token = googleAuthService.googleLogin(principal);
                                response.sendRedirect(frontendUrl + "?token=" + token);
                             })
                     .failureHandler((request, response, exception) -> {
                        response.sendRedirect(frontendUrl + "?error=true");
                    });
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }


}
