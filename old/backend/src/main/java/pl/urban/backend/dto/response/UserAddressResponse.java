package pl.urban.backend.dto.response;

public record UserAddressResponse(
        Long id,
        String street,
        String houseNumber,
        String city,
        String zipCode,
        String phoneNumber,
        String floorNumber,
        String accessCode,
        double latitude,
        double longitude,
        Long userId
) {
}
