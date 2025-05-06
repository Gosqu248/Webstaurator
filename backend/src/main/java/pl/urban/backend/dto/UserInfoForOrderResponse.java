package pl.urban.backend.dto;

public record UserInfoForOrderResponse(
        Long id,
        String name,
        String email,
        String phoneNumber
) {
}
