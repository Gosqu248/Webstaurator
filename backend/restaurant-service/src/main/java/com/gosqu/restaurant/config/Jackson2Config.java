package com.gosqu.restaurant.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4 auto-konfiguruje domyślnie Jackson 3 (tools.jackson.databind.ObjectMapper).
 * Kilka miejsc w tym serwisie (deserializacja eventów Kafka, serializer cache'u Redis)
 * jest napisanych pod klasyczny Jackson 2 (com.fasterxml.jackson.databind) — obecny na
 * classpath tranzytywnie przez jjwt-jackson, ale bez auto-konfigurowanego beana. Zamiast
 * migrować to wszystko na nowe API Jackson 3, wystawiamy jego ObjectMapper jako osobny bean.
 */
@Configuration
public class Jackson2Config {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
