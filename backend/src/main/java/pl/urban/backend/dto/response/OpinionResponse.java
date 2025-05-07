package pl.urban.backend.dto.response;

public record OpinionResponse(
         Long id,
         double qualityRating,
         double deliveryRating
) {
}
