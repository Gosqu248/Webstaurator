package pl.urban.backend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.DeliveryHour;

import java.util.List;

@Repository
public interface DeliveryHourRepository extends JpaRepository<DeliveryHour, Long> {

    List<DeliveryHour> findByRestaurantId(Long restaurantId, Sort sort);
}
