package pl.edu.pjwstk.dusigrosz.service.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.pjwstk.dusigrosz.common.customException.CurrencyException;
import pl.edu.pjwstk.dusigrosz.common.dto.CurrencyDto;
import pl.edu.pjwstk.dusigrosz.common.dto.NbpCurrencyDto;
import pl.edu.pjwstk.dusigrosz.common.dto.NbpTableDto;
import pl.edu.pjwstk.dusigrosz.domain.model.Currency;
import pl.edu.pjwstk.dusigrosz.domain.repository.CurrencyRepository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final static String NBP_URL = "http://api.nbp.pl/api/exchangerates/tables/A?format=json";
    private final RestTemplate restTemplate;

    public List<CurrencyDto> getAllCurrencies(){
        return currencyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CurrencyDto getCurrencyByCode(String code) throws CurrencyException {
        return currencyRepository.findByCodeIgnoreCase(code).map(this::convertToDto)
                .orElseThrow(()->new CurrencyException("Currency not found"));
    }

    @Transactional(rollbackOn = Exception.class)
    public void updateExchangeRates() throws CurrencyException{
        try{
            ResponseEntity<NbpTableDto[]> response = restTemplate.getForEntity(NBP_URL, NbpTableDto[].class);

            if(response.getBody() == null || response.getBody().length == 0){
                throw new CurrencyException("Failed to update exchange rates - no response from NBP");
            }

            List<NbpCurrencyDto> rates = response.getBody()[0].getRates();

            for(NbpCurrencyDto rate : rates){
                Optional<Currency> existingCurrency = currencyRepository.findByCodeIgnoreCase(rate.getCode());

                if(existingCurrency.isPresent()){
                    Currency currency = existingCurrency.get();
                    currency.setExchangeRate(BigDecimal.valueOf(rate.getMid()));
                    currencyRepository.save(currency);
                }else{
                    Currency currency = new Currency();
                    currency.setName(rate.getCurrency());
                    currency.setCode(rate.getCode());
                    currency.setExchangeRate(BigDecimal.valueOf(rate.getMid()));
                    currencyRepository.save(currency);
                }

            }

        }catch (Exception e){
            throw new CurrencyException("Failed to update exchange rates");
        }
    }

    private CurrencyDto convertToDto(Currency currency){
        return new CurrencyDto(
                currency.getId(),
                currency.getName(),
                currency.getCode(),
                currency.getExchangeRate());
    }
}
