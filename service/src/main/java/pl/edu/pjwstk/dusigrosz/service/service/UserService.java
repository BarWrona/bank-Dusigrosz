package pl.edu.pjwstk.dusigrosz.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.dusigrosz.common.customException.UserException;
import pl.edu.pjwstk.dusigrosz.common.dto.UserDto;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.VisorRepository;
import pl.edu.pjwstk.dusigrosz.service.service.UserProfileService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VisorRepository visorRepository;
    private final UserProfileService userProfileService;

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
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
    public UserDto create(UserDto userDto) {
        List<pl.edu.pjwstk.dusigrosz.domain.model.Visor> availableVisors = visorRepository.findAll().stream()
                .filter(v -> v.getUsers().size() < 5)
                .collect(Collectors.toList());
        pl.edu.pjwstk.dusigrosz.domain.model.Visor selectedVisor = null;
        if (!availableVisors.isEmpty()) {
            selectedVisor = availableVisors.get(0);
        } else {
            throw new RuntimeException("No advisors available to assign.");
        }

        User user = convertToEntity(userDto);
        user.setVisor(selectedVisor);

        User savedUser = userRepository.save(user);

        if (userDto.getProfile() != null) {
            try {
                userProfileService.create(savedUser.getId(), userDto.getProfile());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create user profile: " + e.getMessage());
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
                user.getVisor() != null ? user.getVisor().getFirstName() + " " + user.getVisor().getLastName() : null,
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