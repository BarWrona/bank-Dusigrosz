package pl.edu.pjwstk.dusigrosz.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.dusigrosz.common.customException.UserException;
import pl.edu.pjwstk.dusigrosz.common.customException.UserProfileException;
import pl.edu.pjwstk.dusigrosz.common.customException.VisorException;
import pl.edu.pjwstk.dusigrosz.common.dto.UserDto;
import pl.edu.pjwstk.dusigrosz.service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('VISOR')")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('VISOR') or hasAuthority('USER')")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) throws UserException {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Operation(summary = "Create new user")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) throws VisorException, UserProfileException {
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update user data")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto userDto) throws UserException {
        return ResponseEntity.ok(userService.update(id, userDto));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws UserException {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}