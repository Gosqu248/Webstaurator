package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import pl.urban.backend.model.Restaurant;



@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Restaurant findLogoById(Long id);


}
