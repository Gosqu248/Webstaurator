package pl.urban.backend.dto.response;

public record RestaurantAddressResponse(
        Long id,
        String street,
        String flatNumber,
        String city,
        String zipCode,
        double latitude,
        double longitude,
        Long restaurantId
) {
}
