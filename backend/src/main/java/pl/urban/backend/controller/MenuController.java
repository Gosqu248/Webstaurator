package pl.urban.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Menu;
import pl.urban.backend.service.MenuService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("/getRestaurantMenu")
    public List<Menu> getRestaurantMenu(@RequestParam Long restaurantId) {
        return menuService.getRestaurantMenu(restaurantId);
    }

    @GetMapping("/menuCategories")
    public Set<String> getMenuCategories(@RequestParam Long restaurantId) {
        return menuService.getRestaurantMenuCategories(restaurantId);
    }
}
