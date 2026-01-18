package pl.edu.pjwstk.dusigrosz.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjwstk.dusigrosz.domain.model.Visor;

import java.util.Optional;

@Repository
public interface VisorRepository extends JpaRepository<Visor, Long> {
    Optional<Visor> findByUsernameIgnoreCase(String username);
}
