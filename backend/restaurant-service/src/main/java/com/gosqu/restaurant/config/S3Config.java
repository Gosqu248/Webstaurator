package com.gosqu.restaurant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(
            @Value("${aws.s3.endpoint:}") String endpointOverride,
            @Value("${aws.region:us-east-1}") String region) {

        var builder = S3Client.builder().region(Region.of(region));
        if (!endpointOverride.isBlank()) {
            builder.endpointOverride(URI.create(endpointOverride))
                    .forcePathStyle(true);
        }

        return builder.build();
    }
}
