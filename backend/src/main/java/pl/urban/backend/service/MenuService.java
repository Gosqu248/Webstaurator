package pl.urban.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.urban.backend.model.Menu;
import pl.urban.backend.repository.MenuRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

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

}
