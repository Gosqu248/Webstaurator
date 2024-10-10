package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FavouriteRestaurantDTO {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantCategory;
    private String restaurantLogoUrl;

    private String street;
    private String flatNumber;

    private List<OpinionDTO> restaurantOpinion;

}
