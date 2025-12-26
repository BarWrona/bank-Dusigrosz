package pl.edu.pjwstk.bankingdomain.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.bankingcommon.CustomException.UserProfileException;
import pl.edu.pjwstk.bankingdomain.model.User;
import pl.edu.pjwstk.bankingdomain.model.UserProfile;
import pl.edu.pjwstk.bankingdomain.repository.UserProfileRepository;
import pl.edu.pjwstk.bankingdomain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfile getProfileByUserId(Long userId)throws UserProfileException{
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("User profile not found"));
    }
    @Transactional
    public UserProfile create(UserProfile profile, Long userId) throws UserProfileException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("Cannot create profile for non-existing user"));
        if(userProfileRepository.existsById(userId)){
            throw new UserProfileException("User profile already exists");
        }

        profile.setUser(user);
        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile update(Long userId, UserProfile details) throws UserProfileException{
        UserProfile existingProfile = getProfileByUserId(userId);

        existingProfile.setAdditionalDataCollecting(details.isAdditionalDataCollecting());
        existingProfile.setTwoFactorAuth(details.isTwoFactorAuth());
        return userProfileRepository.save(existingProfile);
    }

    @Transactional
    public void delete(Long userId) throws UserProfileException{
        if(!userProfileRepository.existsById(userId)){
            throw new UserProfileException("User profile not found");
        }
        userProfileRepository.deleteById(userId);
    }
}
