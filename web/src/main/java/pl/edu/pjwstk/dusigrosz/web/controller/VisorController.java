package pl.edu.pjwstk.dusigrosz.web.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.dusigrosz.common.customException.VisorException;
import pl.edu.pjwstk.dusigrosz.common.dto.VisorDto;
import pl.edu.pjwstk.dusigrosz.service.service.VisorService;


import java.util.List;

@RestController
@RequestMapping("/visors")
@RequiredArgsConstructor
@Tag(name = "Visors management")
public class VisorController {

    private final VisorService visorService;

    @Operation(summary = "Get all visors")
    @GetMapping
    public ResponseEntity<List<VisorDto>> getAll(){
        return ResponseEntity.ok(visorService.getAll());
    }

    @Operation(summary = "Get visor by id")
    @GetMapping("/{id}")
    public ResponseEntity<VisorDto> getById(@PathVariable Long id) throws VisorException {
        return ResponseEntity.ok(visorService.getById(id));
    }

    @Operation(summary = "Create new visor")
    @PostMapping
    public ResponseEntity<VisorDto> create(@RequestBody VisorDto dto) throws VisorException {
        return new ResponseEntity<>(visorService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete visor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws VisorException {
        visorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update visor if exists")
    @PutMapping("/{id}")
    public ResponseEntity<VisorDto> update(@PathVariable Long id, @RequestBody VisorDto dto) throws VisorException {
        return ResponseEntity.ok(visorService.update(id, dto));
    }
}
