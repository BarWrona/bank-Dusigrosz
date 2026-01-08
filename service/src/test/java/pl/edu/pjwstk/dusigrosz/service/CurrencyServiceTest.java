package pl.edu.pjwstk.dusigrosz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.edu.pjwstk.dusigrosz.common.customException.CurrencyException;
import pl.edu.pjwstk.dusigrosz.common.dto.CurrencyDto;
import pl.edu.pjwstk.dusigrosz.common.dto.NbpCurrencyDto;
import pl.edu.pjwstk.dusigrosz.common.dto.NbpTableDto;
import pl.edu.pjwstk.dusigrosz.domain.model.Currency;
import pl.edu.pjwstk.dusigrosz.domain.repository.CurrencyRepository;
import pl.edu.pjwstk.dusigrosz.service.service.CurrencyService;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyService currencyService;

    private Currency sampleCurrency;

    @BeforeEach
    void setUp(){
        sampleCurrency = new Currency();
        sampleCurrency.setId(1L);
        sampleCurrency.setName("SampleName");
        sampleCurrency.setCode("SMP");
        sampleCurrency.setExchangeRate(BigDecimal.valueOf(3.312));

    }

    @Test
    @DisplayName("Powinien zwrócić listę wszstkich walut")
    void getAll_ShouldReturnListOfDtos(){
        when(currencyRepository.findAll()).thenReturn(List.of(sampleCurrency));

        List<CurrencyDto> result = currencyService.getAllCurrencies();

        assertFalse(result.isEmpty());
        assertEquals("SampleName",result.getFirst().getName());
        verify(currencyRepository).findAll();
    }

    @Test
    @DisplayName("Powinien znaleźć walutę po kodzie i zwrócić DTO, jeśli istnieje")
    void getByCode_ShouldReturnDto_WhenExists() throws CurrencyException {
        when(currencyRepository.findByCodeIgnoreCase("smp")).thenReturn(Optional.of(sampleCurrency));

        CurrencyDto result = currencyService.getCurrencyByCode("smp");

        assertEquals("SampleName",result.getName());
        verify(currencyRepository).findByCodeIgnoreCase("smp");
    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem, jeśli waluta o podanym kodzie nie istnieje")
    void getByCode_ShouldThrowException_WhenNotFound() {
        when(currencyRepository.findByCodeIgnoreCase("smp")).thenReturn(Optional.empty());

        assertThrows(CurrencyException.class, () -> currencyService.getCurrencyByCode("smp"));
        verify(currencyRepository).findByCodeIgnoreCase("smp");
    }

    @Test
    @DisplayName("Powinien zaktualizować kursy walut z NBP")
    void updateExchangeRates_ShouldUpdateExistingAndSaveNew() throws CurrencyException {

        NbpCurrencyDto nbpRate = new NbpCurrencyDto();
        nbpRate.setCode("SMP");
        nbpRate.setCurrency("SampleName");
        nbpRate.setMid(4.15);


        NbpTableDto nbpTable = new NbpTableDto();
        nbpTable.setRates(List.of(nbpRate));
        NbpTableDto[] nbpResponseArray = new NbpTableDto[]{nbpTable};

        when(restTemplate.getForEntity(anyString(), eq(NbpTableDto[].class)))
                .thenReturn(ResponseEntity.ok(nbpResponseArray));
        when(currencyRepository.findByCodeIgnoreCase("SMP")).thenReturn(Optional.of(sampleCurrency));

        currencyService.updateExchangeRates();

        verify(currencyRepository, atLeastOnce()).save(any(Currency.class));
        assertEquals(new BigDecimal("4.15"), sampleCurrency.getExchangeRate());
    }

    @Test
    @DisplayName("Powinien rzucić CurrencyException, gdy odpowiedź z NBP jest pusta")
    void updateExchangeRates_ShouldThrowException_WhenResponseIsEmpty() {

        when(restTemplate.getForEntity(anyString(), eq(NbpTableDto[].class)))
                .thenReturn(ResponseEntity.ok(new NbpTableDto[0]));


        assertThrows(CurrencyException.class, () -> currencyService.updateExchangeRates());
    }

    @Test
    @DisplayName("Powinien rzucić CurrencyException, gdy nie udało się połączyć z NBP")
    void updateExchangeRates_ShouldThrowException_WhenConnectionFails() throws CurrencyException{
        when(restTemplate.getForEntity(anyString(), eq(NbpTableDto[].class)))
                .thenThrow(new RuntimeException("Connection failed"));

        assertThrows(CurrencyException.class, () -> currencyService.updateExchangeRates());
    }
}
