package pl.edu.pjwstk.dusigrosz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjwstk.dusigrosz.common.customException.TransferException;
import pl.edu.pjwstk.dusigrosz.common.dto.TransferDto;
import pl.edu.pjwstk.dusigrosz.common.dto.TransferRequest;
import pl.edu.pjwstk.dusigrosz.domain.model.Account;
import pl.edu.pjwstk.dusigrosz.domain.model.Currency;
import pl.edu.pjwstk.dusigrosz.domain.model.Transfer;
import pl.edu.pjwstk.dusigrosz.domain.repository.AccountRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.TransferRepository;
import pl.edu.pjwstk.dusigrosz.service.service.TransferService;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    private Account sampleSenderAccount;
    private Account sampleReceiverAccount;
    private Currency samplePln;
    private Currency sampleUsd;

    @BeforeEach
    void setUp() {
        samplePln = new Currency();
        samplePln.setCode("PLN");
        samplePln.setExchangeRate(BigDecimal.ONE);

        sampleUsd = new Currency();
        sampleUsd.setCode("USD");
        sampleUsd.setExchangeRate(new BigDecimal("4.00"));

        sampleSenderAccount = new Account();
        sampleSenderAccount.setIban("SENDER-IBAN");
        sampleSenderAccount.setBalance(new BigDecimal("1000.00"));
        sampleSenderAccount.setCurrency(samplePln);

        sampleReceiverAccount = new Account();
        sampleReceiverAccount.setIban("RECEIVER-IBAN");
        sampleReceiverAccount.setBalance(new BigDecimal("0.00"));
        sampleReceiverAccount.setCurrency(samplePln);
    }


    @Test
    @DisplayName("Powinien zwrócić wszystkie transfery")
    void getAll_ShouldReturnList() {
        Transfer sampleTransfer = new Transfer();
        sampleTransfer.setId(1L);
        sampleTransfer.setSenderIban(sampleSenderAccount);
        sampleTransfer.setReceiverIban(sampleReceiverAccount);
        when(transferRepository.findAll()).thenReturn(List.of(sampleTransfer));

        List<TransferDto> result = transferService.getAll();

        assertEquals(1, result.size());
        verify(transferRepository).findAll();
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek, gdy transfer nie istnieje")
    void getTransferById_ShouldThrowException() {
        when(transferRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TransferException.class, () -> transferService.getTransferById(1L));
    }


    @Test
    @DisplayName("Przelew w tej samej walucie - sukces")
    void executeTransfer_SameCurrency_Success() throws TransferException {

        TransferRequest request = new TransferRequest("SENDER-IBAN", "RECEIVER-IBAN", new BigDecimal("100.00"));
        when(accountRepository.findAccountByIban("SENDER-IBAN")).thenReturn(Optional.of(sampleSenderAccount));
        when(accountRepository.findAccountByIban("RECEIVER-IBAN")).thenReturn(Optional.of(sampleReceiverAccount));


        transferService.executeTransfer(request);


        assertEquals(new BigDecimal("900.00"), sampleSenderAccount.getBalance());
        assertEquals(new BigDecimal("100.00"), sampleReceiverAccount.getBalance());
        verify(transferRepository).save(any(Transfer.class));
        verify(accountRepository, times(1)).save(sampleSenderAccount);
        verify(accountRepository, times(1)).save(sampleReceiverAccount);
    }

    @Test
    @DisplayName("Przelew z przewalutowaniem (PLN -> USD) - sukces")
    void executeTransfer_WithConversion_Success() throws TransferException {

        sampleReceiverAccount.setCurrency(sampleUsd);
        TransferRequest request = new TransferRequest("SENDER-IBAN", "RECEIVER-IBAN", new BigDecimal("400.00"));

        when(accountRepository.findAccountByIban("SENDER-IBAN")).thenReturn(Optional.of(sampleSenderAccount));
        when(accountRepository.findAccountByIban("RECEIVER-IBAN")).thenReturn(Optional.of(sampleReceiverAccount));


        transferService.executeTransfer(request);



        assertEquals(new BigDecimal("600.00"), sampleSenderAccount.getBalance());
        assertEquals(0, new BigDecimal("100.00").compareTo(sampleReceiverAccount.getBalance()));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy niewystarczających środkach")
    void executeTransfer_InsufficientFunds_ThrowsException() {
        TransferRequest request = new TransferRequest("SENDER-IBAN", "RECEIVER-IBAN", new BigDecimal("2000.00"));
        when(accountRepository.findAccountByIban("SENDER-IBAN")).thenReturn(Optional.of(sampleSenderAccount));
        when(accountRepository.findAccountByIban("RECEIVER-IBAN")).thenReturn(Optional.of(sampleReceiverAccount));

        TransferException ex = assertThrows(TransferException.class, () -> transferService.executeTransfer(request));
        assertEquals("Insufficient funds", ex.getMessage());
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy próbie przelewu na to samo konto")
    void executeTransfer_ToSameAccount_ThrowsException() {
        TransferRequest request = new TransferRequest("SENDER-IBAN", "SENDER-IBAN", new BigDecimal("100.00"));
        when(accountRepository.findAccountByIban("SENDER-IBAN")).thenReturn(Optional.of(sampleSenderAccount));

        assertThrows(TransferException.class, () -> transferService.executeTransfer(request));
    }


    @Test
    @DisplayName("Kurs dla tej samej waluty powinien wynosić 1")
    void calculateRate_SameCurrency_ReturnsOne() {
        BigDecimal rate = transferService.calculateRate(sampleSenderAccount, sampleReceiverAccount);
        assertEquals(BigDecimal.ONE, rate);
    }

    @Test
    @DisplayName("Kurs dla różnych walut powinien być poprawnie obliczony")
    void calculateRate_DifferentCurrency_ReturnsCorrectRate() {

        sampleReceiverAccount.setCurrency(sampleUsd);

        BigDecimal rate = transferService.calculateRate(sampleSenderAccount, sampleReceiverAccount);

        assertEquals(0, new BigDecimal("0.25").compareTo(rate));
    }
}