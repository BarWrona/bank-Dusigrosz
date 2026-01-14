package pl.edu.pjwstk.dusigrosz.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.dusigrosz.common.customException.VisorException;
import pl.edu.pjwstk.dusigrosz.common.dto.UserDto;
import pl.edu.pjwstk.dusigrosz.common.dto.VisorDto;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.model.Visor;
import pl.edu.pjwstk.dusigrosz.domain.repository.VisorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisorService {

    private final VisorRepository visorRepository;

    public List<VisorDto> getAll() {
        return visorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public VisorDto getById(Long id) throws VisorException {
        return visorRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new VisorException("Visor doesn't exist"));
    }

    public List<UserDto> getAssignedUsers(Long visorId) throws VisorException {
        Visor visor = visorRepository.findById(visorId)
                .orElseThrow(() -> new VisorException("Visor doesn't exist"));

        return visor.getUsers().stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPesel(),
                        user.getPhoneNumber(),
                        user.getUsername(),
                        visor.getFirstName() + " " + visor.getLastName(),
                        visor.getPhoneNumber(),
                        null))
                .collect(Collectors.toList());
    }

    @Transactional
    public VisorDto create(VisorDto dto) {
        Visor visor = new Visor();
        visor.setFirstName(dto.getFirstName());
        visor.setLastName(dto.getLastName());
        visor.setPesel(dto.getPesel());
        visor.setPhoneNumber(dto.getPhoneNumber());

        Visor savedVisor = visorRepository.save(visor);
        return convertToDto(savedVisor);
    }

    @Transactional
    public VisorDto update(Long id, VisorDto dto) throws VisorException {
        Visor visor = visorRepository.findById(id)
                .orElseThrow(() -> new VisorException("Cannot update - visor not found"));

        visor.setFirstName(dto.getFirstName());
        visor.setLastName(dto.getLastName());
        visor.setPhoneNumber(dto.getPhoneNumber());

        return convertToDto(visorRepository.save(visor));
    }

    @Transactional
    public void delete(Long id) throws VisorException {
        if (!visorRepository.existsById(id)) {
            throw new VisorException("Cannot delete - visor not found");
        }
        visorRepository.deleteById(id);
    }

    private VisorDto convertToDto(Visor visor) {
        List<Long> userIds = visor.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        return new VisorDto(
                visor.getId(),
                visor.getFirstName(),
                visor.getLastName(),
                visor.getPhoneNumber(),
                visor.getPesel(),
                userIds);
    }
}