package pl.urban.backend.dto.request;

public record TwoFactorVerificationRequest(
        String email,
        String code
) {
}
