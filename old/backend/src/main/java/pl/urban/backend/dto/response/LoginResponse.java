package pl.urban.backend.dto.response;

public record LoginResponse(
        String accessToken,
        UserResponse user
) {
}
