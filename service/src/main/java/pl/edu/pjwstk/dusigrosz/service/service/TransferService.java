package pl.edu.pjwstk.dusigrosz.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.dusigrosz.common.customException.TransferException;
import pl.edu.pjwstk.dusigrosz.common.dto.TransferDto;
import pl.edu.pjwstk.dusigrosz.common.dto.TransferRequest;
import pl.edu.pjwstk.dusigrosz.domain.model.Account;
import pl.edu.pjwstk.dusigrosz.domain.model.Transfer;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.repository.AccountRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.TransferRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserRepository;
import pl.edu.pjwstk.dusigrosz.service.security.UserDetailsImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public List<TransferDto> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isVisor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VISOR"));
        String currentUsername = authentication.getName();

        return transferRepository.findAll().stream()
                .filter(transfer -> {
                    if (!isVisor)
                        return true;
                    boolean senderMatch = transfer.getSenderIban().getUsers().stream()
                            .anyMatch(u -> u.getVisor() != null
                                    && u.getVisor().getUsername().equalsIgnoreCase(currentUsername));
                    boolean receiverMatch = transfer.getReceiverIban().getUsers().stream()
                            .anyMatch(u -> u.getVisor() != null
                                    && u.getVisor().getUsername().equalsIgnoreCase(currentUsername));

                    return senderMatch || receiverMatch;
                })
                .map(this::convertToDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<TransferDto> findMyTransfers(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElse(null);

        if (user == null)
            return List.of();

        Collection<Account> accounts = user.getAccounts();
        if (accounts.isEmpty())
            return List.of();

        return transferRepository.findBySenderIbanInOrReceiverIbanInOrderByCreatedAtDesc(accounts, accounts).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TransferDto getTransferById(Long id) throws TransferException {
        return transferRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new TransferException("Transfer not found"));
    }

    @Transactional
    public void executeTransfer(TransferRequest request) throws TransferException {
        Account sender = accountRepository.findAccountByIban(request.getSenderIban())
                .orElseThrow(() -> new TransferException("Cannot find sender account"));

        Account receiver = accountRepository.findAccountByIban(request.getReceiverIban())
                .orElseThrow(() -> new TransferException("Cannot find receiver account"));

        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean isOwner = sender.getUsers().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(currentUsername));

        boolean isUserRole = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"));
        if (isUserRole && !isOwner) {
            throw new TransferException("You can only transfer funds from your own accounts.");
        }

        if (sender.equals(receiver)) {
            throw new TransferException("Cannot transfer to same account");
        }

        if (sender.getBalance().compareTo(request.getAmountSent()) < 0) {
            throw new TransferException("Insufficient funds");
        }

        BigDecimal rate = calculateRate(sender, receiver);
        BigDecimal amountReceived = request.getAmountSent().multiply(rate).setScale(2, RoundingMode.FLOOR);

        sender.setBalance(sender.getBalance().subtract(request.getAmountSent()));
        receiver.setBalance(receiver.getBalance().add(amountReceived));

        Transfer transfer = new Transfer();
        transfer.setSenderIban(sender);
        transfer.setReceiverIban(receiver);
        transfer.setAmountSent(request.getAmountSent());
        transfer.setAmountReceived(amountReceived);
        transfer.setExchangeRate(rate);
        transfer.setTitle(request.getTitle());

        if (authentication != null
                && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication
                    .getPrincipal();
            transfer.setExecutorName(userDetails.getUsername());
        } else {
            transfer.setExecutorName("System");
        }

        transferRepository.save(transfer);
        accountRepository.save(sender);
        accountRepository.save(receiver);

    }

    public BigDecimal calculateRate(Account sender, Account receiver) {

        if (sender.getCurrency().getCode().equals(receiver.getCurrency().getCode())) {
            return BigDecimal.ONE;
        }

        return sender.getCurrency().getExchangeRate().divide(receiver.getCurrency().getExchangeRate(), 5,
                RoundingMode.FLOOR);
    }

    public TransferDto convertToDto(Transfer transfer) {
        return new TransferDto(
                transfer.getId(),
                transfer.getSenderIban().getIban(),
                transfer.getReceiverIban().getIban(),
                transfer.getAmountSent(),
                transfer.getAmountReceived(),
                transfer.getExchangeRate(),
                transfer.getCreatedAt(),
                transfer.getTitle());
    }
}
