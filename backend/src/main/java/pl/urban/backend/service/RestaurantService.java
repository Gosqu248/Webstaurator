package pl.urban.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.model.Menu;
import pl.urban.backend.model.OpenHour;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.model.RestaurantAddress;
import pl.urban.backend.repository.MenuRepository;
import pl.urban.backend.repository.OpenHourRepository;
import pl.urban.backend.repository.RestaurantAddressRepository;
import pl.urban.backend.repository.RestaurantRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OpenHourRepository openHourRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantAddressRepository restaurantAddressRepository;

    @Transactional
    public Restaurant addRestaurant(Restaurant restaurant) {

        Set<Menu> updatedMenu = new HashSet<>();
        for (Menu menu : restaurant.getMenu()) {
            Menu existingMenu = menuRepository.findByNameAndIngredientsAndPriceAndImage(
                    menu.getName(), menu.getIngredients(), menu.getPrice(), menu.getImage()
            );

            if (existingMenu != null) {
                updatedMenu.add(existingMenu);
            } else {
                updatedMenu.add(menuRepository.save(menu));
            }
        }
        restaurant.setMenu(updatedMenu);

        if(restaurant.getRestaurantAddress() != null) {
            restaurant.getRestaurantAddress().setRestaurant(restaurant);
        }

        if(restaurant.getOpenHour() != null) {
            for(OpenHour openHour : restaurant.getOpenHour()) {
                openHour.setRestaurant(restaurant);
            }
        }

        return restaurantRepository.save(restaurant);
    }
}