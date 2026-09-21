package com.gosqu.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

// aws.s3.endpoint puste (domyślnie w prod/EKS) -> prawdziwy AWS, domyślny łańcuch poświadczeń
// (IRSA). aws.s3.endpoint ustawione (dev -> floci) -> lokalny endpoint + statyczne poświadczenia
// testowe, bo floci nie waliduje kluczy, ale SDK i tak wymaga jakiegoś providera przed requestem.
@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(
            @Value("${aws.s3.endpoint:}") String endpointOverride,
            @Value("${aws.region:us-east-1}") String region) {
        var builder = S3Client.builder().region(Region.of(region));
        if (!endpointOverride.isBlank()) {
            builder.endpointOverride(URI.create(endpointOverride))
                    .forcePathStyle(true)
                    .credentialsProvider(localCredentials());
        }
        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(
            @Value("${aws.s3.endpoint:}") String endpointOverride,
            @Value("${aws.region:us-east-1}") String region) {
        var builder = S3Presigner.builder().region(Region.of(region));
        if (!endpointOverride.isBlank()) {
            builder.endpointOverride(URI.create(endpointOverride))
                    .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                    .credentialsProvider(localCredentials());
        }
        return builder.build();
    }

    private AwsCredentialsProvider localCredentials() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"));
    }
}
