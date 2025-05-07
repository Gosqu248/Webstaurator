package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public List<Menu> getRestaurantMenu(Long restaurantId) {
        List<Menu> menuItems = menuRepository.findByRestaurantId(restaurantId);
        return sortByCategory(menuItems);
    }


    public Set<String> getRestaurantMenuCategories(Long restaurantId) {
        List<Menu> menuItems = menuRepository.findByRestaurantId(restaurantId);
        return menuItems.stream()
                .map(Menu::getCategory)
                .collect(Collectors.toSet());
    }


    public List<Menu> sortByCategory(List<Menu> menuItems) {
        Map<String, List<Menu>> sortedMenu = menuItems.stream()
                .collect(Collectors.groupingBy(Menu::getCategory, TreeMap::new, Collectors.toList()));

        return sortedMenu.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Transactional
    public Restaurant addMenuToRestaurant(Long restaurantId, Menu menu) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Menu existingMenu = menuRepository.findByNameAndIngredientsAndPriceAndCategory(
                menu.getName(), menu.getIngredients(), menu.getPrice(), menu.getCategory()
        );

        if (existingMenu == null) {
            existingMenu = menuRepository.save(menu);
        }

        restaurant.getMenu().add(existingMenu);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant removeMenuFromRestaurant(Long restaurantId, Long menuId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        restaurant.getMenu().remove(menu);
        return restaurantRepository.save(restaurant);
    }

}
