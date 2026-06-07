package pl.urban.backend.dto.response;

public record DeliveryHourResponse(
         Long id,
         int dayOfWeek,
         String openTime,
         String closeTime,
         Long restaurantId
) {
}
