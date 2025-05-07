package pl.urban.backend.dto.request;

public record LoginRequest(
        String email,
        String password
) {

}
