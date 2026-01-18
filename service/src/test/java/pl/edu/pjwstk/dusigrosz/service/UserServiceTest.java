package pl.edu.pjwstk.dusigrosz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pjwstk.dusigrosz.common.customException.UserException;
import pl.edu.pjwstk.dusigrosz.common.customException.UserProfileException;
import pl.edu.pjwstk.dusigrosz.common.customException.VisorException;
import pl.edu.pjwstk.dusigrosz.common.dto.UserDto;
import pl.edu.pjwstk.dusigrosz.domain.model.Role;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.model.Visor;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.VisorRepository;
import pl.edu.pjwstk.dusigrosz.service.service.UserProfileService;
import pl.edu.pjwstk.dusigrosz.service.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private VisorRepository visorRepository;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private UserDto sampleUserDto;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setFirstName("Jan");
        sampleUser.setLastName("Kowalski");
        sampleUser.setPesel("12345667890");
        sampleUser.setPhoneNumber("500600700");
        sampleUser.setUsername("jan.kowalski");
        sampleUser.setRole(Role.USER);

        sampleUserDto = new UserDto();
        sampleUserDto.setId(1L);
        sampleUserDto.setFirstName("Jan");
        sampleUserDto.setLastName("Kowalski");
        sampleUserDto.setPesel("12345667890");
        sampleUserDto.setPhoneNumber("500600700");
        sampleUserDto.setUsername("jan.kowalski");
    }

    @Test
    @DisplayName("Powinien zwrócić listę wszystkich użytkowników")
    void getAll_ShouldReturnListOfDtos() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        doReturn(List.of(new SimpleGrantedAuthority("ADMIN"))).when(authentication).getAuthorities();

        List<UserDto> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals("Jan", result.getFirst().getFirstName());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Powinien zwrócić UserDto jeśli ID isnieje")
    void getById_ShouldReturnDto_WhenExists() throws UserException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserDto result = userService.getById(1L);

        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Powinien rzucić UserException kiedy użytkownik nie istnieje")
    void getById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.getById(1L));
    }

    @Test
    @DisplayName("Powinien zwrócić użytkownika po username")
    void getByUsername_ShouldReturnDto_WhenExists() throws UserException {

        when(userRepository.findByUsernameIgnoreCase("jan.kowalski")).thenReturn(Optional.of(sampleUser));

        UserDto result = userService.getByUsername("jan.kowalski");

        assertEquals("jan.kowalski", result.getUsername());
        verify(userRepository).findByUsernameIgnoreCase("jan.kowalski");
    }

    @Test
    @DisplayName("Powinien rzucić UserException, gdy nazwa użytkownika nie istnieje")
    void findByUsername_ShouldThrowException_WhenUsernameNotFound() {
        when(userRepository.findByUsernameIgnoreCase("unknown")).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.getByUsername("unknown"));
    }

    @Test
    @DisplayName("Powinien utworzyć nowego użytkownika z zahaszowanym hasłem")
    void create_ShouldSaveAndReturnDto() throws VisorException, UserProfileException {
        Visor sampleVisor = new Visor();
        when(visorRepository.findAll()).thenReturn(List.of(sampleVisor));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        sampleUserDto.setPassword("plainPassword");
        UserDto result = userService.create(sampleUserDto);

        assertEquals("Jan", result.getFirstName());
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(argThat(user -> "encodedPassword".equals(user.getPassword())));
    }

    @Test
    @DisplayName("Powinien usunąć użytkownika, jeśli istnieje")
    void delete_ShouldCallRepository_WhenExists() throws UserException {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);

    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem przy próbie usunięcia nieistniejącego użytkownika")
    void delete_ShouldThrowException_WhenNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserException.class, () -> userService.delete(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Powinien zaktualizować dane użytkownika, jeśli użytkownik istnieje")
    void update_ShouldUpdateAndReturnDto_WhenExists() throws UserException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserDto updateDto = new UserDto();
        updateDto.setFirstName("Adam");

        UserDto result = userService.update(1L, updateDto);

        assertEquals("Adam", result.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek, jeśli użytkownik nieistnieje")
    void update_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.update(1L, new UserDto()));
        verify(userRepository, never()).save(any(User.class));
    }
}
