package pl.edu.pjwstk.bankingdomain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.bankingcommon.CustomException.UserException;
import pl.edu.pjwstk.bankingdomain.model.User;
import pl.edu.pjwstk.bankingdomain.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id)throws UserException{
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User create(User user){
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) throws UserException {
        if(!userRepository.existsById(id)){
            throw new UserException("User not found");
        }
        userRepository.deleteById(id);
    }


    @Transactional
    public User update(Long id, User userDetails) throws UserException{
        User existingUser = getUserById(id);
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        return userRepository.save(existingUser);
    }
}
