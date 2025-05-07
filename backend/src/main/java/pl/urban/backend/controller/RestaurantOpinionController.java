package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.request.RestaurantOpinionRequest;
import pl.urban.backend.dto.response.RestaurantOpinionResponse;
import pl.urban.backend.model.RestaurantOpinion;
import pl.urban.backend.service.RestaurantOpinionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/opinions")
public class RestaurantOpinionController {
    private final RestaurantOpinionService restaurantOpinionService;

    @GetMapping("/restaurantOpinions")
    public List<RestaurantOpinionResponse> getRestaurantOpinion(@RequestParam Long restaurantId) {
        return restaurantOpinionService.getRestaurantOpinion(restaurantId);
    }

    @PostMapping("/addOpinion")
    public RestaurantOpinion addOpinion(@RequestBody RestaurantOpinionRequest opinionRequest, @RequestParam Long orderId) {
        return restaurantOpinionService.addOpinion(opinionRequest, orderId);
    }

    @GetMapping("/getRatingRestaurant")
    public double getRestaurantQualityRating(@RequestParam Long restaurantId) {
        return restaurantOpinionService.getRestaurantRating(restaurantId);
    }
}
