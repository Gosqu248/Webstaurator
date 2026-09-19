package pl.urban.backend.dto.request;


public record FavouriteRequest(
        Long userId,
        Long restaurantId
) {
}
