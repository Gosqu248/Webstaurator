package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.Menu;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Menu findByNameAndIngredientsAndPriceAndCategory(String name, String ingredients, double price, String category);
    List<Menu> findByRestaurantId(Long restaurantId);


}
