package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.RestaurantAddress;

@Repository
public interface RestaurantAddressRepository extends JpaRepository<RestaurantAddress, Long> {

    RestaurantAddress findByRestaurantId(Long restaurantId);
}
