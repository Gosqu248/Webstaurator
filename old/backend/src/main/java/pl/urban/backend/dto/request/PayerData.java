package pl.urban.backend.dto.request;

public record PayerData(
        String email,
        String name,
        String phone
) {
}
