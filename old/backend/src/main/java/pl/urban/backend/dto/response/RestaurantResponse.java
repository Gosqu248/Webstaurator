package pl.urban.backend.dto.response;

public record RestaurantResponse(
        Long id,
        String name,
        String category,
        String logoUrl,
        String imageUrl
) {
}
