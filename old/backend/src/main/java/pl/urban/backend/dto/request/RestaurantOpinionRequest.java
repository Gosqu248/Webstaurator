package pl.urban.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.urban.backend.dto.response.UserNameResponse;


public record RestaurantOpinionRequest(
        @NotNull(message = "Quality rating cannot be null")
        double qualityRating,
        @NotNull(message = "Delivery rating cannot be null")
        double deliveryRating,
        @NotNull(message = "Comment cannot be null")
        String comment,
        UserNameResponse user
) {
}
