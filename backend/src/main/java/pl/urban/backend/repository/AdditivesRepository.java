package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.Additives;

@Repository
public interface AdditivesRepository extends JpaRepository<Additives, Long> {
    Additives findByNameAndValue(String name, String value);
}
