package pl.urban.backend.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavouriteRequest {
    private Long userId;
    private Long restaurantId;

}
