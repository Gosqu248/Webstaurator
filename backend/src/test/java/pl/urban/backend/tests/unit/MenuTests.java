package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.urban.backend.model.Menu;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.repository.MenuRepository;
import pl.urban.backend.repository.RestaurantRepository;
import pl.urban.backend.service.MenuService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuTests {

    private static final Logger logger = LoggerFactory.getLogger(MenuTests.class);

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRestaurantMenu_ShouldReturnSortedMenuItems() {
        Long restaurantId = 1L;
        Menu item1 = new Menu();
        Menu item2 = new Menu();

        item1.setName("Burgerowy król");
        item1.setCategory("Burger");
        item1.setPrice(15.0);

        item2.setName("Salad");
        item2.setCategory("Lettuce");
        item2.setPrice(20.0);
        List<Menu> menuItems = Arrays.asList(item1, item2);

        when(menuRepository.findByRestaurantId(restaurantId)).thenReturn(menuItems);

        logger.info("Fetching menu for restaurant with ID: {}", restaurantId);
        List<Menu> result = menuService.getRestaurantMenu(restaurantId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Burgerowy król", result.getFirst().getName());
    }

    @Test
    void getRestaurantMenuCategories_ShouldReturnUniqueCategories() {
        Long restaurantId = 1L;
        Menu item1 = new Menu();
        Menu item2 = new Menu();

        item1.setName("Burgerowy król");
        item1.setCategory("Burger");
        item1.setPrice(15.0);

        item2.setName("Pizza farmerska");
        item2.setCategory("Pizza");
        item2.setPrice(20.0);

        List<Menu> menuItems = Arrays.asList(item1, item2);

        when(menuRepository.findByRestaurantId(restaurantId)).thenReturn(menuItems);

        logger.info("Fetching menu categories for restaurant with ID: {}", restaurantId);
        Set<String> result = menuService.getRestaurantMenuCategories(restaurantId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Burger"));
        assertTrue(result.contains("Pizza"));
    }

    @Test
    void addMenuToRestaurant_ShouldAddMenuToRestaurant() {
        Long restaurantId = 1L;
        Menu newMenu = new Menu();
        newMenu.setName("Burgerowy król");
        newMenu.setCategory("Burger");
        newMenu.setPrice(15.0);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setMenu(new HashSet<>());

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuRepository.findByNameAndIngredientsAndPriceAndCategory(
                newMenu.getName(), newMenu.getIngredients(), newMenu.getPrice(), newMenu.getCategory()
        )).thenReturn(null); // Simulate no existing menu found
        when(menuRepository.save(newMenu)).thenReturn(newMenu);
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        logger.info("Adding menu item to restaurant with ID: {}", restaurantId);
        Restaurant updatedRestaurant = menuService.addMenuToRestaurant(restaurantId, newMenu);

        assertNotNull(updatedRestaurant, "The updated restaurant should not be null");
        assertEquals(1, updatedRestaurant.getMenu().size(), "The restaurant should have one menu item");
        assertTrue(updatedRestaurant.getMenu().contains(newMenu), "The restaurant's menu should contain the new menu item");

        verify(menuRepository).save(newMenu); // Ensure the menu was saved
        verify(restaurantRepository).save(restaurant); // Ensure the restaurant was saved
    }

    @Test
    void removeMenuFromRestaurant_ShouldRemoveMenuFromRestaurant() {
        Long restaurantId = 1L;
        Long menuId = 2L;

        Menu menu = new Menu();
        menu.setId(menuId);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setMenu(new HashSet<>(Collections.singleton(menu)));

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        logger.info("Removing menu item with ID: {} from restaurant with ID: {}", menuId, restaurantId);
        Restaurant updatedRestaurant = menuService.removeMenuFromRestaurant(restaurantId, menuId);

        assertNotNull(updatedRestaurant, "The updated restaurant should not be null");
        assertTrue(updatedRestaurant.getMenu().isEmpty(), "The restaurant's menu should be empty after removal");

        verify(menuRepository).findById(menuId); // Ensure the menu existence was checked
        verify(restaurantRepository).save(restaurant); // Ensure the restaurant was saved
    }


    @Test
    void removeMenuFromRestaurant_ShouldThrowExceptionIfMenuNotFound() {
        Long restaurantId = 1L;
        Long menuId = 2L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setMenu(new HashSet<>());

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        logger.warn("Attempting to remove non-existent menu item with ID: {} from restaurant with ID: {}", menuId, restaurantId);
        assertThrows(IllegalArgumentException.class, () -> menuService.removeMenuFromRestaurant(restaurantId, menuId));
    }
}
