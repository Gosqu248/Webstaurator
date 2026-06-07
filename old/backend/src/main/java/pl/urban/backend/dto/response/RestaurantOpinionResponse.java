package pl.urban.backend.dto.response;

import java.time.ZonedDateTime;

public record RestaurantOpinionResponse(
        Long id,
        double qualityRating,
        double deliveryRating,
        String comment,
        ZonedDateTime createdAt,
        UserNameResponse user
) {
}
