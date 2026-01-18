package pl.edu.pjwstk.dusigrosz.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjwstk.dusigrosz.domain.model.Account;
import pl.edu.pjwstk.dusigrosz.domain.model.Transfer;

import java.util.Collection;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
   List<Transfer> findBySenderIbanInOrReceiverIbanInOrderByCreatedAtDesc(
           Collection<Account> senderIbans,
            Collection<Account> receiverIbans);
}
