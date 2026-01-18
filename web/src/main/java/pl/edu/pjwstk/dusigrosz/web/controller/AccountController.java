package pl.edu.pjwstk.dusigrosz.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.dusigrosz.common.customException.AccountException;
import pl.edu.pjwstk.dusigrosz.common.customException.CurrencyException;
import pl.edu.pjwstk.dusigrosz.common.customException.UserException;
import pl.edu.pjwstk.dusigrosz.common.dto.AccountDto;
import pl.edu.pjwstk.dusigrosz.service.security.UserDetailsImpl;
import pl.edu.pjwstk.dusigrosz.service.service.AccountService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts management")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Show all accounts")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('VISOR')")
    public ResponseEntity<List<AccountDto>> getAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @Operation(summary = "Get my accounts")
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<AccountDto>> getMyAccounts() throws UserException {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(accountService.findMyAccounts(username));
    }

    @Operation(summary = "Create new account")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<AccountDto> create(@RequestBody AccountDto dto) throws UserException, CurrencyException, AccountException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isAdmin) {
           UserDetailsImpl userDetails = (UserDetailsImpl) authentication
                    .getPrincipal();
            Collections.singleton(userDetails.getId());
            dto.setUserIds(Collections.singleton(userDetails.getId()));
        }

        return new ResponseEntity<>(accountService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get account by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority()")
    public ResponseEntity<AccountDto> getById(@PathVariable Long id) throws AccountException {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @Operation(summary = "Delete account")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws AccountException {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
