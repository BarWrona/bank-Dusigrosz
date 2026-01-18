package pl.edu.pjwstk.dusigrosz.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pjwstk.dusigrosz.common.customException.CurrencyException;
import pl.edu.pjwstk.dusigrosz.common.customException.UserException;
import pl.edu.pjwstk.dusigrosz.common.customException.AccountException;
import pl.edu.pjwstk.dusigrosz.common.dto.AccountDto;
import pl.edu.pjwstk.dusigrosz.domain.model.Account;
import pl.edu.pjwstk.dusigrosz.domain.model.Currency;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.repository.AccountRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.CurrencyRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserRepository;
import pl.edu.pjwstk.dusigrosz.service.util.BankAccountsIbanGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;
    private final BankAccountsIbanGenerator bankAccountsIbanGenerator;

    public List<AccountDto> findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isVisor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VISOR"));
        String currentUsername = authentication.getName();

        List<Account> accounts;
        if (isVisor) {
            accounts = accountRepository.findAllByUsersVisorUsername(currentUsername);
        } else {
            accounts = accountRepository.findAll();
        }

        return accounts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AccountDto> findMyAccounts(String username) throws UserException {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserException("User not found"));

        return user.getAccounts().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountDto create(AccountDto dto) throws CurrencyException, AccountException, UserException {
        Currency currency = currencyRepository.findById(dto.getCurrencyId())
                .orElseThrow(() -> new CurrencyException("Currency not exist"));

        String newIban = bankAccountsIbanGenerator.generateIban();

        Account account = new Account();
        account.setIban(newIban);
        account.setCurrency(currency);
        account.setBalance(dto.getBalance());

        account = accountRepository.save(account);

        if (dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
            throw new AccountException("Cannot create account without owners");
        }

        for (Long userId : dto.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException("User not exist"));

            user.getAccounts().add(account);
            account.getUsers().add(user);
        }

        return convertToDto(account);
    }

    public AccountDto getById(Long id) throws AccountException {
        return accountRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new AccountException("Account not found"));
    }

    @Transactional
    public void delete(Long id) throws AccountException {
        if (!accountRepository.existsById(id)) {
            throw new AccountException("Cannot delete - account not found");
        }
        if (!accountRepository.findById(id).get().getBalance().equals(BigDecimal.ZERO)) {
            throw new AccountException("Cannot delete - account has non-zero balance");
        }
        accountRepository.deleteById(id);
    }

    public AccountDto convertToDto(Account account) {
        Set<Long> userIds = account.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return new AccountDto(
                account.getIban(),
                account.getCurrency().getId(),
                account.getCurrency().getCode(),
                account.getBalance(),
                userIds);
    }

}
