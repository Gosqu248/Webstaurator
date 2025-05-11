package pl.urban.backend.dto.request;


public record PasswordResetConfirmRequest(
        String token,
        String newPassword
) {
}
