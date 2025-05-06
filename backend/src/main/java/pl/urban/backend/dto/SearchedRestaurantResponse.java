package pl.urban.backend.dto;

public record SearchedRestaurantResponse(
        Long restaurantId,
        String name,
        String category,
        boolean pickup,
        double distance,
        double rating,
        double deliveryPrice,
        double lat,
        double lon
) {
}
