package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchedRestaurantDTO {
    private Long id;
    private String street;
    private String flatNumber;
    private String city;
    private String zipCode;
    private double distance;
    private Long restaurantId;
}
