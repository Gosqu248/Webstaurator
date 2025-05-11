package pl.urban.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record PasswordResetRequest(
        @NotNull(message = "Email cannot be null")
        String email
) {
}
