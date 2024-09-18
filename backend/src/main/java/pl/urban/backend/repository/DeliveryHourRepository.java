package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.DeliveryHour;

@Repository
public interface DeliveryHourRepository extends JpaRepository<DeliveryHour, Long> {
}
