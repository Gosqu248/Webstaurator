package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findByRestaurantId(Long restaurantId);
}
