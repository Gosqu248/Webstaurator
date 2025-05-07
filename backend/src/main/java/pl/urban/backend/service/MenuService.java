package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.dto.response.MenuResponse;
import pl.urban.backend.dto.response.RestaurantResponse;
import pl.urban.backend.model.Menu;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.repository.MenuRepository;
import pl.urban.backend.repository.RestaurantRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final MapperService mapper;

    public List<MenuResponse> getRestaurantMenu(Long restaurantId) {
        List<MenuResponse> menuItems = menuRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(mapper::fromMenu)
                .collect(Collectors.toList());
        return sortByCategory(menuItems);
    }

    public Set<String> getRestaurantMenuCategories(Long restaurantId) {
        List<Menu> menuItems = menuRepository.findByRestaurantId(restaurantId);
        return menuItems.stream()
                .map(Menu::getCategory)
                .collect(Collectors.toSet());
    }

    public List<MenuResponse> sortByCategory(List<MenuResponse> menuItems) {
        Map<String, List<MenuResponse>> sortedMenu = menuItems.stream()
                .collect(Collectors.groupingBy(MenuResponse::category, TreeMap::new, Collectors.toList()));

        return sortedMenu.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantResponse addMenuToRestaurant(Long restaurantId, Menu menu) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Menu existingMenu = menuRepository.findByNameAndIngredientsAndPriceAndCategory(
                menu.getName(), menu.getIngredients(), menu.getPrice(), menu.getCategory()
        );

        if (existingMenu == null) {
            existingMenu = menuRepository.save(menu);
        }

        restaurant.getMenu().add(existingMenu);
        return mapper.fromRestaurant(restaurantRepository.save(restaurant));
    }

    @Transactional
    public RestaurantResponse removeMenuFromRestaurant(Long restaurantId, Long menuId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        restaurant.getMenu().remove(menu);
        return mapper.fromRestaurant(restaurantRepository.save(restaurant));
    }

}
