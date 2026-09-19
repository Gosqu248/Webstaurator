package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.UserAddress;

import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    @Query("SELECT u FROM UserAddress u WHERE u.user.id = :userId AND "
            + "(6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * "
            + "cos(radians(u.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(u.latitude)))) < :radiusKm")
    List<UserAddress> findAvailableAddresses(@Param("userId") Long userId,
                                             @Param("latitude") double latitude,
                                             @Param("longitude") double longitude,
                                             @Param("radiusKm") double radiusKm);

}
