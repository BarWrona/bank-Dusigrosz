package pl.edu.pjwstk.bankingdomain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.bankingcommon.CustomException.UserProfileException;
import pl.edu.pjwstk.bankingdomain.model.User;
import pl.edu.pjwstk.bankingdomain.model.UserProfile;
import pl.edu.pjwstk.bankingdomain.repository.UserProfileRepository;
import pl.edu.pjwstk.bankingdomain.repository.UserRepository;
import pl.edu.pjwstk.bankingservice.dto.UserProfileDto;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileDto getProfileByUserId(Long userId) throws UserProfileException {
        return userProfileRepository.findById(userId)
                .map(this::convertToDto)
                .orElseThrow(() -> new UserProfileException("User profile not found"));
    }

    @Transactional
    public UserProfileDto create(Long userId, UserProfileDto dto) throws UserProfileException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("Cannot create profile for non-existing user"));

        if (userProfileRepository.existsById(userId)) {
            throw new UserProfileException("User profile already exists");
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setAdditionalDataCollecting(dto.isAdditionalDataCollecting());
        profile.setTwoFactorAuth(dto.isTwoFactorEnabled());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return convertToDto(savedProfile);
    }

    @Transactional
    public UserProfileDto update(Long userId, UserProfileDto dto) throws UserProfileException {
        UserProfile existingProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("User profile not found"));

        existingProfile.setAdditionalDataCollecting(dto.isAdditionalDataCollecting());
        existingProfile.setTwoFactorAuth(dto.isTwoFactorEnabled());

        UserProfile updatedProfile = userProfileRepository.save(existingProfile);
        return convertToDto(updatedProfile);
    }

    @Transactional
    public void delete(Long userId) throws UserProfileException {
        if (!userProfileRepository.existsById(userId)) {
            throw new UserProfileException("User profile not found");
        }
        userProfileRepository.deleteById(userId);
    }

    private UserProfileDto convertToDto(UserProfile userProfile) {
        return new UserProfileDto(
                userProfile.getId(),
                userProfile.isAdditionalDataCollecting(),
                userProfile.isTwoFactorAuth()
        );
    }
}