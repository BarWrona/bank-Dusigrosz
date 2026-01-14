package pl.edu.pjwstk.dusigrosz.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjwstk.dusigrosz.common.dto.VisorDto;
import pl.edu.pjwstk.dusigrosz.service.service.VisorService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(VisorController.class)
public class VisorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisorService visorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllVisors() throws Exception{
        VisorDto dto = new VisorDto();
        when(visorService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/visors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetVisorById() throws Exception{
        Long id = 1L;
        VisorDto dto = new VisorDto();
        when(visorService.getById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/visors/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateVisor() throws Exception{
        VisorDto inputDto = new VisorDto();
        VisorDto savedDto = new VisorDto();

        when(visorService.create(inputDto)).thenReturn(savedDto);

        mockMvc.perform(post("/api/visors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateVisor() throws Exception{
        Long id = 1L;
        VisorDto dto = new VisorDto();

        when(visorService.update(eq(id),any(VisorDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/visors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteVisor() throws Exception{
        Long id = 1L;

        mockMvc.perform(delete("/api/visors/{id}", id))
                .andExpect(status().isNoContent());
    }
}
