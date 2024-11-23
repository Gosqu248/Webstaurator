package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.RestaurantAddress;

import java.util.List;

@Repository
public interface RestaurantAddressRepository extends JpaRepository<RestaurantAddress, Long> {
    RestaurantAddress findByRestaurantId(Long restaurantId);

    @Query("SELECT r FROM RestaurantAddress r WHERE "
            + "r.latitude BETWEEN :minLat AND :maxLat AND "
            + "r.longitude BETWEEN :minLon AND :maxLon AND "
            + "(6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * "
            + "cos(radians(r.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(r.latitude)))) < :radius")
    List<RestaurantAddress> findNearbyRestaurants(@Param("latitude") double latitude,
                                                  @Param("longitude") double longitude,
                                                  @Param("radius") double radius,
                                                  @Param("minLat") double minLat,
                                                  @Param("maxLat") double maxLat,
                                                  @Param("minLon") double minLon,
                                                  @Param("maxLon") double maxLon);
}

