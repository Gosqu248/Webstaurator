package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.FavouriteRestaurant;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRestaurantRepository extends JpaRepository<FavouriteRestaurant, Long> {
    List<FavouriteRestaurant> findByUserId(Long userId);
    Optional<FavouriteRestaurant> findFirstByUserIdAndRestaurantId(Long userId, Long restaurantId);

}
