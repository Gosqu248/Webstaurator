package pl.urban.backend.dto.response;

import java.util.List;
public record FavouriteRestaurantResponse(
         Long id,
         Long restaurantId,
         String restaurantName,
         String restaurantCategory,
         String restaurantLogoUrl,
         String street,
         String flatNumber,

         List<OpinionResponse> restaurantOpinion
) {


}
