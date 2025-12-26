package pl.edu.pjwstk.bankingdomain.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.bankingdomain.model.Visor;
import pl.edu.pjwstk.bankingdomain.repository.VisorRepository;
import pl.edu.pjwstk.bankingcommon.CustomException.VisorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisorService {
    private final VisorRepository visorRepository;


    public Visor create(Visor visor){
        return visorRepository.save(visor);
    }

    public Visor findById(Long id) throws VisorException {
        return visorRepository.findById(id)
                .orElseThrow(() -> new VisorException("Visor not found"));
    }

    public List<Visor> findAll(){
        return visorRepository.findAll();
    }

    @Transactional
    public Visor update(Long id, Visor details) throws VisorException{
        Visor existingVisor = findById(id);
        existingVisor.setFirstName(details.getFirstName());
        existingVisor.setLastName(details.getLastName());
        existingVisor.setPesel(details.getPesel());
        existingVisor.setPhoneNumber(details.getPhoneNumber());
        return visorRepository.save(existingVisor);
    }

    @Transactional
    public void delete(Long id) throws VisorException{
        if(!visorRepository.existsById(id)){
            throw new VisorException("Visor not found");
        }
        visorRepository.deleteById(id);
    }

}
