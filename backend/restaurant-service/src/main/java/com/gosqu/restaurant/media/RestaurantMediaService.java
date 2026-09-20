package com.gosqu.restaurant.media;

import com.gosqu.restaurant.media.dto.response.LogoUploadResponse;
import com.gosqu.restaurant.media.dto.response.PresignedUrlResponse;
import com.gosqu.restaurant.media.exception.LogoNotFoundException;
import com.gosqu.restaurant.restaurant.Restaurant;
import com.gosqu.restaurant.restaurant.RestaurantRepository;
import com.gosqu.restaurant.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantMediaService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/webp");
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/png", ".png",
            "image/jpeg", ".jpg",
            "image/webp", ".webp"
    );

    private final RestaurantService restaurantService;
    private final RestaurantRepository restaurantRepository;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.presign-ttl-minutes:15}")
    private long presignTtlMinutes;

    @Transactional
    @CacheEvict(value = "restaurant", key = "#restaurantId")
    public LogoUploadResponse uploadLogo(UUID ownerId, UUID restaurantId, MultipartFile file) {
        validate(file);
        Restaurant restaurant = restaurantService.findOrThrowOwned(ownerId, restaurantId);

        String key = buildKey(restaurantId, file.getContentType());
        putObject(key, file);

        restaurant.setLogoUrl(key);
        restaurantRepository.save(restaurant);

        log.info("action=restaurant_logo_uploaded restaurantId={} key={}", restaurantId, key);
        return new LogoUploadResponse(key, presign(key));
    }

    @Transactional(readOnly = true)
    public PresignedUrlResponse getLogoUrl(UUID restaurantId) {
        String key = restaurantService.findOrThrow(restaurantId).getLogoUrl();
        if (key == null || key.isBlank()) {
            throw new LogoNotFoundException(restaurantId);
        }
        return new PresignedUrlResponse(presign(key));
    }

    private void putObject(String key, MultipartFile file) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded logo file", e);
        }
    }

    private String presign(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignTtlMinutes))
                .getObjectRequest(GetObjectRequest.builder().bucket(bucket).key(key).build())
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private String buildKey(UUID restaurantId, String contentType) {
        return "restaurants/%s/logo-%s%s".formatted(restaurantId, UUID.randomUUID(), EXTENSIONS.get(contentType));
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File exceeds maximum size of 5MB");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported content type: " + file.getContentType());
        }
    }
}
