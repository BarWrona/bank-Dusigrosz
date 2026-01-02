package pl.edu.pjwstk.bankingweb.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.bankingcommon.CustomException.AccountException;
import pl.edu.pjwstk.bankingcommon.CustomException.CurrencyException;
import pl.edu.pjwstk.bankingdomain.service.AccountService;
import pl.edu.pjwstk.bankingservice.dto.AccountDto;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts management")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Show all accounts")
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @Operation(summary = "Create new account")
    @PostMapping
    public ResponseEntity<AccountDto> create(@RequestBody AccountDto dto) throws AccountException, CurrencyException {
        return new ResponseEntity<>(accountService.create(dto) ,HttpStatus.CREATED);
    }

    @Operation(summary = "Get account by id")
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable Long id) throws AccountException {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @Operation(summary = "Delete account")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws AccountException {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
