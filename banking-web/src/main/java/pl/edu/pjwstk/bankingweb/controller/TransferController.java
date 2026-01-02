package pl.edu.pjwstk.bankingweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.bankingcommon.CustomException.TransferException;
import pl.edu.pjwstk.bankingdomain.service.TransferService;
import pl.edu.pjwstk.bankingservice.dto.TransferDto;
import pl.edu.pjwstk.bankingservice.dto.TransferRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transfers")
@Tag(name = "Transfers management")
public class TransferController {
    private final TransferService transferService;

    @Operation(summary = "Get all transfers")
    @GetMapping
    public ResponseEntity<List<TransferDto>> getAll(){
        return ResponseEntity.ok(transferService.getAll());
    }

    @Operation(summary = "Get transfer by id")
    @GetMapping("/{id}")
    public ResponseEntity<TransferDto> getTransferById(@PathVariable Long id) throws TransferException {
        return ResponseEntity.ok(transferService.getTransferById(id));
    }

    @Operation(summary = "Execute the transfer")
    @PostMapping
    public ResponseEntity<String> executeTransfer(@RequestBody TransferRequest request) throws TransferException {
        transferService.executeTransfer(request);
        return ResponseEntity.ok("Transfer executed successfully");
    }
}
