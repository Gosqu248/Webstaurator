package pl.urban.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserAddressRequest(
        @NotNull(message = "Street cannot be null")
        String street,
        @NotNull(message = "House number cannot be null")
        String houseNumber,
        @NotNull(message = "City cannot be null")
        String city,
        @NotNull(message = "Zip code cannot be null")
        String zipCode,
        String phoneNumber,
        String floorNumber,
        String accessCode
) {
}
