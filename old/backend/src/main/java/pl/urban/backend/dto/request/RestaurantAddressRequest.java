package pl.urban.backend.dto.request;

public record RestaurantAddressRequest(
        String street,
        String flatNumber,
        String city,
        String zipCode
) {
}
