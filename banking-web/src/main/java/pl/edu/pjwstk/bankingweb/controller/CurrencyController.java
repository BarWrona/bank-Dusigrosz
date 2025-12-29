package pl.edu.pjwstk.bankingweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.bankingcommon.CustomException.CurrencyException;
import pl.edu.pjwstk.bankingdomain.model.Currency;
import pl.edu.pjwstk.bankingdomain.service.CurrencyService;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
@Tag(name = "Currencies management")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(summary = "Get all currencies")
    @GetMapping
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @Operation(summary = "Get currency by code")
    @GetMapping("/{code}")
    public ResponseEntity<Currency> getCurrencyByCode(@PathVariable String code) throws CurrencyException {
        return ResponseEntity.ok(currencyService.getCurrencyByCode(code));
    }

    @Operation(summary = "Update exchange rates from NBP")
    @PostMapping("/update-rates")
    public ResponseEntity<String> updateExchangeRates() throws CurrencyException {
        currencyService.updateExchangeRates();
        return ResponseEntity.ok("Rates updated");
    }

}
