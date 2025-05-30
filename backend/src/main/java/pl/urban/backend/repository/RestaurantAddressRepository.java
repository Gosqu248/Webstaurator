package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.RestaurantAddress;

import java.util.List;

@Repository
public interface RestaurantAddressRepository extends JpaRepository<RestaurantAddress, Long> {
    @Query("SELECT r FROM RestaurantAddress r WHERE "
            + "r.latitude BETWEEN :minLat AND :maxLat AND "
            + "r.longitude BETWEEN :minLon AND :maxLon")
    List<RestaurantAddress> findNearbyRestaurants(@Param("minLat") double minLat,
                                                   @Param("maxLat") double maxLat,
                                                   @Param("minLon") double minLon,
                                                   @Param("maxLon") double maxLon);

    RestaurantAddress findByRestaurantId(Long restaurantId);


}

