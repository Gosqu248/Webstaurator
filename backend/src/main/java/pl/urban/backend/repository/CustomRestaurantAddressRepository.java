package pl.urban.backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.RestaurantAddress;

import java.util.List;

@Repository
public class CustomRestaurantAddressRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantAddress> findNearbyRestaurants(double minLat, double maxLat, double minLon, double maxLon) {
        String sql = "SELECT * FROM webs.restaurant_addresses r WHERE "
                + "r.latitude BETWEEN :minLat AND :maxLat AND "
                + "r.longitude BETWEEN :minLon AND :maxLon";

        Query query = entityManager.createNativeQuery(sql, RestaurantAddress.class);
        query.setParameter("minLat", minLat);
        query.setParameter("maxLat", maxLat);
        query.setParameter("minLon", minLon);
        query.setParameter("maxLon", maxLon);

        return query.getResultList();
    }

}
