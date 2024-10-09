package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.FavouriteRestaurant;

import java.util.List;

@Repository
public interface FavouriteRestaurantRepository extends JpaRepository<FavouriteRestaurant, Long> {
    List<FavouriteRestaurant> findByUserId(Long userId);
}
