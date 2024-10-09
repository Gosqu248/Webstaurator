package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantOpinionDTO {

    private Long id;
    private double qualityRating;
    private double deliveryRating;
    
}
