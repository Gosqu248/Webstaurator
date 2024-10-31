package pl.urban.backend.controller;

import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.RestaurantOpinionDTO;
import pl.urban.backend.service.RestaurantOpinionService;

import java.util.List;

@RestController
@RequestMapping("/api/opinions")
public class RestaurantOpinionController {

    private final RestaurantOpinionService restaurantOpinionService;

    public RestaurantOpinionController(RestaurantOpinionService restaurantOpinionService) {
        this.restaurantOpinionService = restaurantOpinionService;
    }

    @GetMapping("/all")
    public List<RestaurantOpinionDTO> getRestaurantOpinion(@RequestParam Long restaurantId) {
        return restaurantOpinionService.getRestaurantOpinion(restaurantId);
    }
}
