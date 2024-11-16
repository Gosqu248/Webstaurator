package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchedRestaurantDTO {
    private Long restaurantId;
    private String name;
    private String category;
    private boolean pickup;
    private double latitude;
    private double longitude;
    private double distance;
}
