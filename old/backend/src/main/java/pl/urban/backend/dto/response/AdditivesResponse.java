package pl.urban.backend.dto.response;

public record AdditivesResponse(
        Long id,
        String name,
        String value,
        double price
) {
}
