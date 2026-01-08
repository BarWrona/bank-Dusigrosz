package pl.edu.pjwstk.dusigrosz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjwstk.dusigrosz.common.customException.UserProfileException;
import pl.edu.pjwstk.dusigrosz.common.dto.UserProfileDto;
import pl.edu.pjwstk.dusigrosz.domain.model.User;
import pl.edu.pjwstk.dusigrosz.domain.model.UserProfile;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserProfileRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserRepository;
import pl.edu.pjwstk.dusigrosz.service.service.UserProfileService;
import pl.edu.pjwstk.dusigrosz.service.service.UserService;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @InjectMocks
    private UserService userService;

    private UserProfile sampleUserProfile;
    private UserProfileDto sampleUserProfileDto;
    private User sampleUser;
    private final static Long SAMPLE_USER_ID = 1L;

    @BeforeEach
    void setUp(){
        sampleUser = new User();
        sampleUser.setId(SAMPLE_USER_ID);
        sampleUser.setUsername("test");

        sampleUserProfile = new UserProfile();
        sampleUserProfile.setId(SAMPLE_USER_ID);
        sampleUserProfile.setAdditionalDataCollecting(true);
        sampleUserProfile.setTwoFactorAuth(true);
        sampleUserProfile.setUser(sampleUser);

        sampleUserProfileDto = new UserProfileDto(
                SAMPLE_USER_ID,
                true,
                true);

    }

    @Test
    @DisplayName("Powinien zwrócić profil użytkownika (dto), gdy istnieje")
    void getProfileByUserId_ShouldReturnDto_WhenExists() throws UserProfileException {
        when(userProfileRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(sampleUserProfile));

        UserProfileDto result = userProfileService.getProfileByUserId(SAMPLE_USER_ID);

        assertEquals(SAMPLE_USER_ID, result.getUserId());
        assertTrue(result.isAdditionalDataCollecting());
        verify(userProfileRepository).findById(SAMPLE_USER_ID);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem, jeśli pofil nie istnieje")
    void getProfileByUserId_ShouldThrowException_WhenNotFound() {
        when(userProfileRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserProfileException.class, () -> userProfileService.getProfileByUserId(SAMPLE_USER_ID));
    }

    @Test
    @DisplayName("Powinien stworzyć profil dla istniejącego użytkownika")
    void createProfileForUser_ShouldCreateProfile_WhenUserExists() throws UserProfileException {
        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(sampleUser));
        when(userProfileRepository.existsById(SAMPLE_USER_ID)).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(sampleUserProfile);

        UserProfileDto result = userProfileService.create(SAMPLE_USER_ID, sampleUserProfileDto);

        assertNotNull(result);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem, jeśli użytkownik nie istnieje")
    void createProfileForUser_ShouldThrowException_WhenUserNotExists() {
        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserProfileException.class, () -> userProfileService.create(SAMPLE_USER_ID, sampleUserProfileDto));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem, jeśli profil użytkownika już istnieje")
    void createProfileForUser_ShouldThrowException_WhenProfileExists() {
        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(sampleUser));
        when(userProfileRepository.existsById(SAMPLE_USER_ID)).thenReturn(true);

        assertThrows(UserProfileException.class, () -> userProfileService.create(SAMPLE_USER_ID, sampleUserProfileDto));
    }

    @Test
    @DisplayName("Powinien zaktualizować istniejący profil")
    void updateUserProfile_ShouldUpdateProfile_WhenExists() throws UserProfileException {
        when(userProfileRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(sampleUserProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(sampleUserProfile);

        UserProfileDto updateDto = new UserProfileDto(SAMPLE_USER_ID, false, false);
        UserProfileDto result = userProfileService.update(SAMPLE_USER_ID, updateDto);

        assertNotNull(result);
        verify(userProfileRepository).save(sampleUserProfile);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem przy próbie aktualizacji nieistniejącego profilu")
    void updateUserProfile_ShouldThrowException_WhenNotFound() {
        when(userProfileRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserProfileException.class, () -> userProfileService.update(SAMPLE_USER_ID, sampleUserProfileDto));
    }

    @Test
    @DisplayName("Powinien usunąć profil użytkownika, jeśli istnieje")
    void deleteProfile_ShouldDeleteProfile_WhenExists() throws UserProfileException {
        when(userProfileRepository.existsById(SAMPLE_USER_ID)).thenReturn(true);

        userProfileService.delete(SAMPLE_USER_ID);

        verify(userProfileRepository).deleteById(SAMPLE_USER_ID);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątkiem, jeśli profil użytkownika nieistnieje")
    void deleteProfile_ShouldThrowException_WhenNotFound() {
        when(userProfileRepository.existsById(SAMPLE_USER_ID)).thenReturn(false);

        assertThrows(UserProfileException.class, () -> userProfileService.delete(SAMPLE_USER_ID));
    }

}
