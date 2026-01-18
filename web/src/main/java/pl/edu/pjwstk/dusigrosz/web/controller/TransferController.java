package pl.edu.pjwstk.dusigrosz.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.dusigrosz.common.customException.TransferException;
import pl.edu.pjwstk.dusigrosz.common.dto.TransferDto;
import pl.edu.pjwstk.dusigrosz.common.dto.TransferRequest;
import pl.edu.pjwstk.dusigrosz.service.service.TransferService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfers")
@Tag(name = "Transfers management")
public class TransferController {
    private final TransferService transferService;

    @Operation(summary = "Get all transfers")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('VISOR')")
    public ResponseEntity<List<TransferDto>> getAll() {
        return ResponseEntity.ok(transferService.getAll());
    }

    @Operation(summary = "Get my transfers")
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<TransferDto>> getMyTransfers() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        return ResponseEntity.ok(transferService.findMyTransfers(authentication.getName()));
    }

    @Operation(summary = "Get transfer by id")
    @GetMapping("/{id}")
    public ResponseEntity<TransferDto> getTransferById(@PathVariable Long id) throws TransferException {
        return ResponseEntity.ok(transferService.getTransferById(id));
    }

    @Operation(summary = "Execute the transfer")
    @PostMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<String> executeTransfer(@RequestBody TransferRequest request) throws TransferException {
        transferService.executeTransfer(request);
        return ResponseEntity.ok("Transfer executed successfully");
    }
}
