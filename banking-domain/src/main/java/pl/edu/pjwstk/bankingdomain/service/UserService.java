package pl.edu.pjwstk.bankingdomain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.bankingcommon.CustomException.UserException;
import pl.edu.pjwstk.bankingdomain.model.User;
import pl.edu.pjwstk.bankingdomain.repository.UserRepository;
import pl.edu.pjwstk.bankingservice.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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

    @Transactional
    public UserDto create(UserDto userDto) {
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto) throws UserException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));

        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setUsername(userDto.getUsername());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());

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
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getPesel(), user.getPhoneNumber());
    }

    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setPesel(dto.getPesel());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }
}