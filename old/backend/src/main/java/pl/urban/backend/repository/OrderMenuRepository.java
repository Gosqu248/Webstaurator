package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.OrderMenu;
@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {
}
