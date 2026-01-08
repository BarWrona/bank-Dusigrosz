package pl.edu.pjwstk.dusigrosz.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjwstk.dusigrosz.domain.model.UserProfile;


@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
