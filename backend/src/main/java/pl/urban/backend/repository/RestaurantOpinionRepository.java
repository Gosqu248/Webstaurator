package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.RestaurantOpinion;

import java.util.List;

@Repository
public interface RestaurantOpinionRepository extends JpaRepository<RestaurantOpinion, Long> {
    List<RestaurantOpinion> findAllByRestaurantId(Long restaurantId);
}
