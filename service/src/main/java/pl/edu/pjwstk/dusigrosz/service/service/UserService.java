package pl.edu.pjwstk.dusigrosz.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.dusigrosz.common.customException.UserException;
import pl.edu.pjwstk.dusigrosz.common.customException.UserProfileException;
import pl.edu.pjwstk.dusigrosz.common.customException.VisorException;
import pl.edu.pjwstk.dusigrosz.common.dto.UserDto;
import pl.edu.pjwstk.dusigrosz.domain.model.Role;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.model.Visor;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.VisorRepository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VisorRepository visorRepository;
    private final UserProfileService userProfileService;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isVisor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VISOR"));

        List<User> users;
        if (isVisor) {
            users = userRepository.findByVisorUsernameIgnoreCase(currentUsername);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Long id) throws UserException {
        return userRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new UserException("User not found"));
    }

    public UserDto getByUsername(String username) throws UserException {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(this::convertToDto)
                .orElseThrow(() -> new UserException("User not found"));
    }

    @Transactional
    public UserDto create(UserDto userDto) throws VisorException, UserProfileException {
        if (userRepository.findByUsernameIgnoreCase(userDto.getUsername()).isPresent()) {
            throw new UserProfileException("Username already exists");
        }

        List<Visor> availableVisors = visorRepository.findAll().stream()
                .filter(v -> v.getUsers().size() < 5)
                .collect(Collectors.toList());
        Visor selectedVisor = null;
        if (!availableVisors.isEmpty()) {
            selectedVisor = availableVisors.get(0);
        } else {
            throw new VisorException("No visors available to assign.");
        }

        User user = convertToEntity(userDto);
        user.setVisor(selectedVisor);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userDto.getRole() != null) {
            try {
                user.setRole(Role.valueOf(userDto.getRole()));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.USER);
            }
        } else {
            user.setRole(Role.USER);
        }

        User savedUser = userRepository.save(user);

        if (userDto.getProfile() != null) {
            try {
                userProfileService.create(savedUser.getId(), userDto.getProfile());
            } catch (Exception e) {
                throw new UserProfileException("Failed to create user profile: " + e.getMessage());
            }
        }

        return convertToDto(savedUser);
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto) throws UserException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));

        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setUsername(userDto.getUsername());

        return convertToDto(userRepository.save(existingUser));
    }

    @Transactional
    public void delete(Long id) throws UserException {
        if (!userRepository.existsById(id)) {
            throw new UserException("User not found");
        }
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPesel(),
                user.getPhoneNumber(),
                user.getUsername(),
                null,
                user.getRole().name(),
                user.getVisor() != null ? user.getVisor().getFirstName() : null,
                user.getVisor() != null ? user.getVisor().getPhoneNumber() : null,
                null);
    }

    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPesel(dto.getPesel());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUsername(dto.getUsername());
        return user;
    }
}