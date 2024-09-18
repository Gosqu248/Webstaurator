package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Menu findByNameAndIngredientsAndPriceAndImage(String name, String ingredients, double price, String image);
}
