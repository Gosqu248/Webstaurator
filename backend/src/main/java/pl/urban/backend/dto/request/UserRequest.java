package pl.urban.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotNull(message = "Name cannot be null")
        String name,
        @NotNull(message = "Email cannot be null")
        String email,
        @NotNull(message = "Password cannot be null")
        String password
) {

}
