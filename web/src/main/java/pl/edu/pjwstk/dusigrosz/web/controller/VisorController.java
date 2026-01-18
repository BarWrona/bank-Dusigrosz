package pl.edu.pjwstk.dusigrosz.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.dusigrosz.common.customException.VisorException;
import pl.edu.pjwstk.dusigrosz.common.dto.VisorDto;
import pl.edu.pjwstk.dusigrosz.service.service.VisorService;

import java.util.List;

@RestController
@RequestMapping("/api/visors")
@RequiredArgsConstructor
@Tag(name = "Visors management")
public class VisorController {

    private final VisorService visorService;

    @Operation(summary = "Get all visors")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<VisorDto>> getAll() {
        return ResponseEntity.ok(visorService.getAll());
    }

    @Operation(summary = "Get visor by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VisorDto> getById(@PathVariable Long id) throws VisorException {
        return ResponseEntity.ok(visorService.getById(id));
    }

    @Operation(summary = "Create new visor")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VisorDto> create(@Valid @RequestBody VisorDto dto) throws VisorException {
        return new ResponseEntity<>(visorService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete visor")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws VisorException {
        visorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update visor if exists")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VisorDto> update(@PathVariable Long id, @RequestBody VisorDto dto) throws VisorException {
        return ResponseEntity.ok(visorService.update(id, dto));
    }
}
