package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.urban.backend.model.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

}
