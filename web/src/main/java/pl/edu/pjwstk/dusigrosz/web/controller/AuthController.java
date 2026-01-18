package pl.edu.pjwstk.dusigrosz.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pjwstk.dusigrosz.common.dto.JwtResponse;
import pl.edu.pjwstk.dusigrosz.common.dto.LoginRequest;
import pl.edu.pjwstk.dusigrosz.common.dto.UserDto;
import pl.edu.pjwstk.dusigrosz.service.security.JwtUtils;
import pl.edu.pjwstk.dusigrosz.service.security.UserDetailsImpl;
import pl.edu.pjwstk.dusigrosz.service.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final JwtUtils jwtUtils;
        private final UserService userService;

        @PostMapping("/login")
        @Operation(summary = "Authenticate user and return JWT token")
        public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(item -> item.getAuthority())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(jwt,
                                userDetails.getId(),
                                userDetails.getUsername(),
                                roles.get(0)));
        }

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
                try {
                        return ResponseEntity.ok(userService.create(userDto));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
                }
        }
}
