package pl.edu.pjwstk.bankingdomain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.bankingcommon.CustomException.AccountException;
import pl.edu.pjwstk.bankingcommon.CustomException.CurrencyException;
import pl.edu.pjwstk.bankingdomain.model.Account;
import pl.edu.pjwstk.bankingdomain.model.Currency;
import pl.edu.pjwstk.bankingdomain.model.User;
import pl.edu.pjwstk.bankingdomain.repository.AccountRepository;
import pl.edu.pjwstk.bankingdomain.repository.CurrencyRepository;
import pl.edu.pjwstk.bankingdomain.repository.UserRepository;
import pl.edu.pjwstk.bankingservice.dto.AccountDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    public List<AccountDto> findAll(){
        return accountRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountDto create(AccountDto dto) throws CurrencyException {
        Currency currency = currencyRepository.findById(dto.getCurrencyId())
                .orElseThrow(() -> new CurrencyException("Currency not exist"));

        Account account = new Account();
        account.setIban(dto.getIban());
        account.setCurrency(currency);
        account.setBalance(dto.getBalance());

        account = accountRepository.save(account);

        if(dto.getUserIds() != null && !dto.getUserIds().isEmpty()){
            for(Long userId : dto.getUserIds()){
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new CurrencyException("User not exist"));

                user.getAccounts().add(account);
                account.getUsers().add(user);
            }
        }

        Account savedAccount = accountRepository.save(account);
        return convertToDto(savedAccount);
    }

    public AccountDto getById(Long id) throws AccountException {
        return accountRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new AccountException("Account not found"));
    }

    @Transactional
    public void delete(Long id) throws AccountException {
        if(!accountRepository.existsById(id)){
            throw new AccountException("Cannot delete - account not found");
        }
        accountRepository.deleteById(id);
    }

    public AccountDto convertToDto(Account account){
        Set<Long> userIds = account.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return new AccountDto(
                account.getIban(),
                account.getCurrency().getId(),
                account.getBalance(),
                userIds
        );
    }


}
