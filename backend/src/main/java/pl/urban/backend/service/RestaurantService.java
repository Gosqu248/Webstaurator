package pl.urban.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.model.DeliveryHour;
import pl.urban.backend.model.Menu;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.repository.MenuRepository;
import pl.urban.backend.repository.RestaurantRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class RestaurantService {

     private final RestaurantRepository restaurantRepository;
     private final MenuRepository menuRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
    }


    @Transactional
    public Restaurant addRestaurant(Restaurant restaurant) {

        if (restaurant.getDelivery() != null) {
            restaurant.getDelivery().setRestaurant(restaurant);
        }

        if (restaurant.getRestaurantAddress() != null) {
            restaurant.getRestaurantAddress().setRestaurant(restaurant);
        }

        Set<Menu> updatedMenu = new HashSet<>();
        for (Menu menu : restaurant.getMenu()) {
            Menu existingMenu = menuRepository.findByNameAndIngredientsAndPriceAndCategory(
                    menu.getName(), menu.getIngredients(), menu.getPrice(), menu.getCategory()
            );

            updatedMenu.add(Objects.requireNonNullElseGet(existingMenu, () -> menuRepository.save(menu)));
        }
        restaurant.setMenu(updatedMenu);


        if (restaurant.getDeliveryHours() != null) {
            restaurant.getDeliveryHours().forEach(deliveryHour -> deliveryHour.setRestaurant(restaurant));
        }

        if (restaurant.getRestaurantOpinions() != null) {
            restaurant.getRestaurantOpinions().forEach(restaurantOpinion -> restaurantOpinion.setRestaurant(restaurant));
        }

        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllDeliveryRestaurants() {
        return restaurantRepository.findAllDeliveryRestaurants();
    }

    public List<Restaurant> getAllPickupRestaurants() {
        return restaurantRepository.findAllPickupRestaurants();
    }

    public Set<String> getDeliveryCategories() {
        return restaurantRepository.findDeliveryCategories();
    }

    public Set<String> getPickupCategories() {
        return restaurantRepository.findPickupCategories();
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id).orElse(null);
    }
}