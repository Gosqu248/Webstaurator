package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.response.MenuResponse;
import pl.urban.backend.dto.response.RestaurantResponse;
import pl.urban.backend.model.Menu;
import pl.urban.backend.service.MenuService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/getRestaurantMenu")
    public ResponseEntity<List<MenuResponse>> getMenuByRestaurantId(@RequestParam Long restaurantId) {
        try {
            return ResponseEntity.ok(menuService.getRestaurantMenu(restaurantId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/menuCategories")
    public Set<String> getMenuCategories(@RequestParam Long restaurantId) {
        return menuService.getRestaurantMenuCategories(restaurantId);
    }
    @PostMapping("/{restaurantId}/addMenu")
    public ResponseEntity<RestaurantResponse> addMenuToRestaurant(@PathVariable Long restaurantId, @RequestBody Menu menu) {
        try {
            RestaurantResponse updatedRestaurant = menuService.addMenuToRestaurant(restaurantId, menu);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{restaurantId}/removeMenu/{menuId}")
    public ResponseEntity<RestaurantResponse> removeMenuFromRestaurant(@PathVariable Long restaurantId, @PathVariable Long menuId) {
        try {
            return ResponseEntity.ok(menuService.removeMenuFromRestaurant(restaurantId, menuId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
