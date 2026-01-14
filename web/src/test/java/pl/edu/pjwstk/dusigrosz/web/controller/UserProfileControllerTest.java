package pl.edu.pjwstk.dusigrosz.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjwstk.dusigrosz.common.dto.UserProfileDto;
import pl.edu.pjwstk.dusigrosz.service.service.UserProfileService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetProfile() throws Exception {
        Long userId = 1L;
        UserProfileDto dto = new UserProfileDto();
        when(userProfileService.getProfileByUserId(userId)).thenReturn(dto);

        mockMvc.perform(get("/api/user-profiles/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldCreateProfile() throws Exception {
        Long userId = 1L;
        UserProfileDto dto = new UserProfileDto();
        when(userProfileService.create(eq(userId), any(UserProfileDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/user-profiles/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateProfile() throws Exception {
        Long userId = 1L;
        UserProfileDto dto = new UserProfileDto();
        when(userProfileService.update(eq(userId), any(UserProfileDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/user-profiles/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteProfile() throws Exception {
        Long userId = 1L;
        doNothing().when(userProfileService).delete(userId);

        mockMvc.perform(delete("/api/user-profiles/{userId}", userId))
                .andExpect(status().isNoContent());
    }
}