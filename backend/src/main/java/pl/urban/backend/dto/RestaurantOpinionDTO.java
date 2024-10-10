package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class RestaurantOpinionDTO {

    private Long id;

    private double qualityRating;
    private double deliveryRating;
    private String comment;
    private ZonedDateTime createdAt;
    private UserNameDTO user;


}
