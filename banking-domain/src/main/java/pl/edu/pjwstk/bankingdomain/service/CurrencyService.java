package pl.edu.pjwstk.bankingdomain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.pjwstk.bankingcommon.CustomException.CurrencyException;
import pl.edu.pjwstk.bankingdomain.model.Currency;
import pl.edu.pjwstk.bankingdomain.repository.CurrencyRepository;
import pl.edu.pjwstk.bankingservice.dto.CurrencyDto;
import pl.edu.pjwstk.bankingservice.dto.NbpTableDto;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final static String NBP_URL = "http://api.nbp.pl/api/exchangerates/tables/A?format=json";
    private final RestTemplate restTemplate;

    public List<Currency> getAllCurrencies(){
        return currencyRepository.findAll();
    }

    public Currency getCurrencyByCode(String code) throws CurrencyException {
        return currencyRepository.findByCodeIgnoreCase(code)
                .orElseThrow(()->new CurrencyException("Currency not found"));
    }

    @Transactional(rollbackOn = Exception.class)
    public void updateExchangeRates() throws CurrencyException{
        try{
            ResponseEntity<NbpTableDto[]> response = restTemplate.getForEntity(NBP_URL, NbpTableDto[].class);

            if(response.getBody() == null || response.getBody().length == 0){
                throw new CurrencyException("Failed to update exchange rates - no response from NBP");
            }

            List<CurrencyDto> rates = response.getBody()[0].getRates();

            for(CurrencyDto rate : rates){
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
}
