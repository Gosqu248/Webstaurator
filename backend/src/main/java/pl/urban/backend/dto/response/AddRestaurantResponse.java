package pl.urban.backend.dto.response;

import pl.urban.backend.model.Delivery;
import pl.urban.backend.model.DeliveryHour;
import pl.urban.backend.model.Menu;
import pl.urban.backend.model.RestaurantAddress;

import java.util.List;
import java.util.Set;

public record AddRestaurantResponse(
        String name,
        String category,

        String logoUrl,
        String imageUrl,
        RestaurantAddress restaurantAddress,

        List<String> paymentMethods,

        Delivery delivery,
        List<DeliveryHour> deliveryHours,
        Set<Menu> menu
) {
}
