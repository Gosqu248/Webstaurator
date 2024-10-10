package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpinionDTO {

    private Long id;
    private double qualityRating;
    private double deliveryRating;
    
}
