package pl.edu.pjwstk.dusigrosz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjwstk.dusigrosz.common.customException.VisorException;
import pl.edu.pjwstk.dusigrosz.common.dto.UserDto;
import pl.edu.pjwstk.dusigrosz.common.dto.VisorDto;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.model.Visor;
import pl.edu.pjwstk.dusigrosz.domain.repository.VisorRepository;
import pl.edu.pjwstk.dusigrosz.service.service.VisorService;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisorServiceTest {

    @Mock
    private VisorRepository visorRepository;

    @InjectMocks
    private VisorService visorService;

    private Visor sampleVisor;
    private VisorDto sampleVisorDto;

    @BeforeEach
    void setUp() {
        sampleVisor = new Visor();
        sampleVisor.setId(1L);
        sampleVisor.setFirstName("Jan");
        sampleVisor.setLastName("Kowalski");
        sampleVisor.setPesel("12345678901");
        sampleVisor.setPhoneNumber("500600700");
        sampleVisor.setUsers(new ArrayList<>());

        sampleVisorDto = new VisorDto();
        sampleVisorDto.setFirstName("Jan");
        sampleVisorDto.setLastName("Kowalski");
        sampleVisorDto.setPesel("12345678901");
        sampleVisorDto.setPhoneNumber("500600700");
    }

    @Test
    @DisplayName("Powinien zwrócić listę wszystkich opiekunów")
    void getAll_ShouldReturnListOfDtos() {
        // Given
        when(visorRepository.findAll()).thenReturn(List.of(sampleVisor));

        // When
        List<VisorDto> result = visorService.getAll();

        // Then
        assertEquals(1, result.size());
        assertEquals("Jan", result.get(0).getFirstName());
        verify(visorRepository).findAll();
    }

    @Test
    @DisplayName("Powinien zwrócić VisorDto po ID")
    void getById_ShouldReturnDto_WhenExists() throws VisorException {
        // Given
        when(visorRepository.findById(1L)).thenReturn(Optional.of(sampleVisor));

        // When
        VisorDto result = visorService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(visorRepository).findById(1L);
    }

    @Test
    @DisplayName("Powinien rzucić VisorException przy braku ID")
    void getById_ShouldThrowException_WhenNotFound() {
        // Given
        when(visorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(VisorException.class, () -> visorService.getById(99L));
    }

    @Test
    @DisplayName("Powinien zwrócić listę przypisanych użytkowników")
    void getAssignedUsers_ShouldReturnUserDtoList() throws VisorException {
        // Given
        User user = new User();
        user.setId(10L);
        user.setFirstName("Adam");
        sampleVisor.getUsers().add(user);

        when(visorRepository.findById(1L)).thenReturn(Optional.of(sampleVisor));

        // When
        List<UserDto> result = visorService.getAssignedUsers(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals("Adam", result.getFirst().getFirstName());
    }

    @Test
    @DisplayName("Powinien poprawnie stworzyć nowego opiekuna")
    void create_ShouldSaveAndReturnDto() {
        // Given
        when(visorRepository.save(any(Visor.class))).thenReturn(sampleVisor);

        // When
        VisorDto result = visorService.create(sampleVisorDto);

        // Then
        assertNotNull(result);
        assertEquals("Jan", result.getFirstName());
        verify(visorRepository).save(any(Visor.class));
    }

    @Test
    @DisplayName("Powinien usunąć opiekuna, jeśli istnieje")
    void delete_ShouldCallRepository_WhenExists() throws VisorException {
        // Given
        when(visorRepository.existsById(1L)).thenReturn(true);

        // When
        visorService.delete(1L);

        // Then
        verify(visorRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy próbie usunięcia nieistniejącego opiekuna")
    void delete_ShouldThrowException_WhenNotFound() {
        // Given
        when(visorRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(VisorException.class, () -> visorService.delete(1L));
        verify(visorRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Powinien zrobić update Visora, jeśli istnieje")
    void update_ShouldUpdateAndReturnDto_WhenExists() throws VisorException{
        when(visorRepository.findById(1L)).thenReturn(Optional.of(sampleVisor));
        when(visorRepository.save(any(Visor.class))).thenReturn(sampleVisor);

        VisorDto updateDto = new VisorDto();
        updateDto.setFirstName("Adam");

        VisorDto result = visorService.update(1L, updateDto);

        assertEquals("Adam", result.getFirstName());
        verify(visorRepository).save(any(Visor.class));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem przy próbie modyfikacji nieistniejącego Visora")
    void update_ShouldThrowException_WhenNotFound(){
        when(visorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(VisorException.class, () -> visorService.update(1L, new VisorDto()));
        verify(visorRepository, never()).save(any(Visor.class));
    }

}