package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.OpenHour;
@Repository
public interface OpenHourRepository extends JpaRepository<OpenHour, Long> {
}
