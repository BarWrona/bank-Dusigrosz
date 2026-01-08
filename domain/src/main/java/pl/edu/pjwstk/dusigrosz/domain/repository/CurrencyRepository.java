package pl.edu.pjwstk.dusigrosz.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjwstk.dusigrosz.domain.model.Currency;


import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCodeIgnoreCase(String code);
}
