package pl.edu.pjwstk.bankingweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.bankingcommon.CustomException.CurrencyException;
import pl.edu.pjwstk.bankingdomain.model.Currency;
import pl.edu.pjwstk.bankingdomain.service.CurrencyService;

import java.util.List;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor

public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @Operation(description = "Update exchange rates")
    @PostMapping("/update-rates")
    public ResponseEntity<String> updateExchangeRates() throws CurrencyException {
        currencyService.updateExchangeRates();
        return ResponseEntity.ok("Rates updated");
    }

}
