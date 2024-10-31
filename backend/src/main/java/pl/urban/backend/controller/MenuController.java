package pl.urban.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Menu;
import pl.urban.backend.service.MenuService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/getRestaurantMenu")
    public ResponseEntity<List<Menu>> getMenuByRestaurantId(@RequestParam Long restaurantId) {
        try {
            List<Menu> menus = menuService.getRestaurantMenu(restaurantId);
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/menuCategories")
    public Set<String> getMenuCategories(@RequestParam Long restaurantId) {
        return menuService.getRestaurantMenuCategories(restaurantId);
    }
}
