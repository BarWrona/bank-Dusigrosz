package pl.edu.pjwstk.bankingdomain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjwstk.bankingdomain.model.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

}
