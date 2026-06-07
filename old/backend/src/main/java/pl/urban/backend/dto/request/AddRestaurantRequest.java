package pl.urban.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.urban.backend.model.Delivery;
import pl.urban.backend.model.DeliveryHour;
import pl.urban.backend.model.Menu;
import pl.urban.backend.model.RestaurantAddress;

import java.util.List;
import java.util.Set;

public record AddRestaurantRequest(
        @NotNull(message = "Name cannot be null")
        String name,
        @NotNull(message = "Category cannot be null")
        String category,

        String logoUrl,
        String imageUrl,
        @NotNull(message = "Restaurant address cannot be null")
        RestaurantAddress restaurantAddress,

        @NotNull(message = "Restaurant payment method cannot be null")
        List<String> paymentMethods,

        @NotNull(message = "Restaurant delivery cannot be null")
        Delivery delivery,
        List<DeliveryHour> deliveryHours,
        @NotNull(message = "Restaurant menu cannot be null")
        Set<Menu> menu
) {
}
