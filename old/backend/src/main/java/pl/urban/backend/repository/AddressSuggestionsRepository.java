package pl.urban.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.backend.model.AddressSuggestions;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressSuggestionsRepository extends JpaRepository<AddressSuggestions, Long> {
    Optional<AddressSuggestions> findByName(String name);

    List<AddressSuggestions> findTop5ByNameContainingIgnoreCase(String partialName);
}
