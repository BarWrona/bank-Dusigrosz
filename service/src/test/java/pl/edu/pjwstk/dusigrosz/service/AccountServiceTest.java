package pl.edu.pjwstk.dusigrosz.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjwstk.dusigrosz.common.customException.AccountException;
import pl.edu.pjwstk.dusigrosz.common.customException.CurrencyException;
import pl.edu.pjwstk.dusigrosz.common.customException.UserException;
import pl.edu.pjwstk.dusigrosz.common.dto.AccountDto;
import pl.edu.pjwstk.dusigrosz.domain.model.Account;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.repository.AccountRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.CurrencyRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserRepository;
import pl.edu.pjwstk.dusigrosz.service.service.AccountService;
import pl.edu.pjwstk.dusigrosz.domain.model.Currency;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CurrencyRepository currencyRepository;

    @InjectMocks
    AccountService accountService;

    private User sampleUser;
    private Currency sampleCurrency;
    private Account sampleAccount;
    private AccountDto sampleAccountDto;

    @BeforeEach
    void setUp(){
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setAccounts(new HashSet<>());

        sampleCurrency = new Currency();
        sampleCurrency.setId(1L);
        sampleCurrency.setCode("SMP");
        sampleCurrency.setName("SampleName");
        sampleCurrency.setExchangeRate(new BigDecimal("3.312"));


        sampleAccount = new Account();
        sampleAccount.setIban("SMP1234567890");
        sampleAccount.setBalance(BigDecimal.ZERO);
        sampleAccount.setCurrency(sampleCurrency);
        sampleAccount.setUsers(new HashSet<>(Collections.singletonList(sampleUser)));

        sampleAccountDto = new AccountDto();
        sampleAccountDto.setIban("SMP1234567890");
        sampleAccountDto.setBalance(BigDecimal.ZERO);
        sampleAccountDto.setCurrencyId(1L);
        sampleAccountDto.setUserIds(Set.of(1L));
    }

    @Test
    @DisplayName("Powinien zwrócić listę wszystkich istniejących kont")
    void findAll_ShouldReturnListOfDtos(){
        when(accountRepository.findAll()).thenReturn(List.of(sampleAccount));

        List<AccountDto> result = accountService.findAll();

        assertEquals(1, result.size());
        verify(accountRepository).findAll();
    }

    @Test
    @DisplayName("Powinien zwrócić AccountDto jeśli konto o podanym ID istnieje")
    void findById_ShouldReturnAccountDto_WhenExists() throws AccountException {
        when(accountRepository.findById(1L)).thenReturn(of(sampleAccount));

        AccountDto result = accountService.getById(1L);

        assertEquals("SMP1234567890", result.getIban());
        verify(accountRepository).findById(1L);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem, jeśli konto o podanym ID nieistnieje")
    void findById_ShouldThrowException_WhenNotFound() {
        when(accountRepository.findById(1L)).thenReturn(empty());

        assertThrows(AccountException.class, () -> accountService.getById(1L));
    }

    @Test
    @DisplayName("Powinien stworzyć konto, gdy dane są poprawne")
    void create_ShouldSaveAccount_WhenDataIsValid() throws CurrencyException, UserException, AccountException {
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(sampleCurrency));
        when(userRepository.findById(1L)).thenReturn(of(sampleUser));
        when(accountRepository.save(any(Account.class))).thenReturn(sampleAccount);

        AccountDto result = accountService.create(sampleAccountDto);

        assertNotNull(result);
        assertEquals("SMP1234567890", result.getIban());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Powinien rzucić CurrencyException, gdy waluta nie istnieje")
    void create_ShouldThrowCurrencyException_WhenCurrencyNotFound() {
        when(currencyRepository.findById(anyLong())).thenReturn(empty());
        assertThrows(CurrencyException.class, () -> accountService.create(sampleAccountDto));
    }

    @Test
    @DisplayName("Powinien rzucić UserException, gdy lista użytkowników jest pusta")
    void create_ShouldThrowUserException_WhenUserIdsEmpty() {
        sampleAccountDto.setUserIds(Collections.emptySet());
        when(currencyRepository.findById(1L)).thenReturn(of(sampleCurrency));

        assertThrows(AccountException.class, () -> accountService.create(sampleAccountDto));
    }

    @Test
    @DisplayName("Powinien rzucić UserException, gdy użytkownik nie istnieje")
    void create_ShouldThrowUserException_WhenUserNotFound() {
        when(currencyRepository.findById(1L)).thenReturn(of(sampleCurrency));
        when(userRepository.findById(1L)).thenReturn(empty());

        assertThrows(UserException.class, () -> accountService.create(sampleAccountDto));
    }

    @Test
    @DisplayName("Powinien usunąć konto, gdy saldo wynosi 0")
    void delete_ShouldRemove_WhenBalanceIsZero() throws AccountException {
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.findById(1L)).thenReturn(of(sampleAccount));

        accountService.delete(1L);

        verify(accountRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek, gdy saldo jest różne od 0")
    void delete_ShouldThrowException_WhenBalanceNotZero() {
        sampleAccount.setBalance(BigDecimal.TEN);
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.findById(1L)).thenReturn(of(sampleAccount));

        AccountException ex = assertThrows(AccountException.class, () -> accountService.delete(1L));
        assertTrue(ex.getMessage().contains("non-zero balance"));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy usuwaniu nieistniejącego konta")
    void delete_ShouldThrowException_WhenAccountNotFound() {
        when(accountRepository.existsById(1L)).thenReturn(false);
        assertThrows(AccountException.class, () -> accountService.delete(1L));
    }


}
