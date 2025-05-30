package pl.urban.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record TwoFactorVerificationRequest(
        @NotNull(message = "Email cannot be null")
        String email,
        @NotNull(message = "Code cannot be null")
        String code
) {
}
