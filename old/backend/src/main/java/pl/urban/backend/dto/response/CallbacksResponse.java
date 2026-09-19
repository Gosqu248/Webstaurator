package pl.urban.backend.dto.response;

public record CallbacksResponse(
        String successUrl,
        String errorUrl,
        String notificationUrl
) {
}
