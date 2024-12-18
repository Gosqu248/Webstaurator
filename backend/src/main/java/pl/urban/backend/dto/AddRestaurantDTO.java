package pl.urban.backend.dto;

import lombok.Data;
import pl.urban.backend.model.*;

import java.util.List;
import java.util.Set;

@Data
public class AddRestaurantDTO {
    private String name;
    private String category;
    private String logoUrl;
    private String imageUrl;
    private RestaurantAddress restaurantAddress;
    private Set<String> paymentMethods;
    private Delivery delivery;
    private List<DeliveryHour> deliveryHours;
    private List<Menu> menu;
}
