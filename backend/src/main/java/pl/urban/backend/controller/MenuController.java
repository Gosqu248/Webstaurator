package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Menu;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.service.MenuService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService menuService;

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
    @PostMapping("/{restaurantId}/addMenu")
    public ResponseEntity<Restaurant> addMenuToRestaurant(@PathVariable Long restaurantId, @RequestBody Menu menu) {
        try {
            Restaurant updatedRestaurant = menuService.addMenuToRestaurant(restaurantId, menu);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{restaurantId}/removeMenu/{menuId}")
    public ResponseEntity<Restaurant> removeMenuFromRestaurant(@PathVariable Long restaurantId, @PathVariable Long menuId) {
        try {
            Restaurant updatedRestaurant = menuService.removeMenuFromRestaurant(restaurantId, menuId);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
