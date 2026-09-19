package pl.urban.backend.dto.response;

import java.util.List;
import java.util.Set;

public record AddRestaurantResponse(
        String name,
        String category,
        String logoUrl,
        String imageUrl,
        RestaurantAddressResponse restaurantAddress,
        List<String> paymentMethods,
        DeliveryResponse delivery,
        List<DeliveryHourResponse> deliveryHours,
        Set<MenuResponse> menu
) {
}
