package pl.urban.backend.dto.response;

public record UserInfoForOrderResponse(
        Long id,
        String name,
        String email,
        String phoneNumber
) {
}
