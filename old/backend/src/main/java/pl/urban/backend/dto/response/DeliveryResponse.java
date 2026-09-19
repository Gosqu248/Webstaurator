package pl.urban.backend.dto.response;
public record DeliveryResponse(
         Long id,
         int deliveryMinTime,
         int deliveryMaxTime,
         double deliveryPrice,
         double minimumPrice,
         int pickupTime,
         Long restaurantId
) {
}
