package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.urban.backend.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByEmail(String email);
}
