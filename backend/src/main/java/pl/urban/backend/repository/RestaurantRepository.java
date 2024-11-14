package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.Restaurant;

import java.util.List;
import java.util.Set;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.menu WHERE r.delivery.deliveryMaxTime > 0")
    List<Restaurant> findAllDeliveryRestaurants();

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.menu WHERE r.delivery.pickupTime > 0")
    List<Restaurant> findAllPickupRestaurants();

    @Query("SELECT DISTINCT r.category FROM Restaurant r LEFT JOIN r.delivery d WHERE d.deliveryMaxTime > 0")
    Set<String> findDeliveryCategories();

    @Query("SELECT DISTINCT r.category FROM Restaurant r LEFT JOIN r.delivery d WHERE d.pickupTime > 0")
    Set<String> findPickupCategories();

    Restaurant findByName(String name);
}
