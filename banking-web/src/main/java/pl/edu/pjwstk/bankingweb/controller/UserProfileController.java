package pl.edu.pjwstk.bankingweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.bankingcommon.CustomException.UserProfileException;
import pl.edu.pjwstk.bankingservice.dto.UserProfileDto;
import pl.edu.pjwstk.bankingdomain.service.UserProfileService;

@RestController
@RequestMapping("/user-profiles")
@RequiredArgsConstructor
@Tag(name = "User Profile Management")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "Get user profile by id")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable Long userId) throws UserProfileException {
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
    }

    @Operation(summary = "Create new user profile")
    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileDto> createProfile(@PathVariable Long userId, @RequestBody UserProfileDto dto) throws UserProfileException {
        return new ResponseEntity<>(userProfileService.create(userId, dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update profile")
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> updateProfile(@PathVariable Long userId, @RequestBody UserProfileDto dto) throws UserProfileException {
        return ResponseEntity.ok(userProfileService.update(userId, dto));
    }

    @Operation(summary = "Delete profile")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) throws UserProfileException {
        userProfileService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}