package pl.edu.pjwstk.dusigrosz.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjwstk.dusigrosz.common.dto.TransferDto;
import pl.edu.pjwstk.dusigrosz.common.dto.TransferRequest;
import pl.edu.pjwstk.dusigrosz.service.service.TransferService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllTransfers() throws Exception {
        TransferDto transfer = new TransferDto();
        when(transferService.getAll()).thenReturn(List.of(transfer));

        mockMvc.perform(get("/api/transfers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetTransferById() throws Exception {
        Long id = 1L;
        TransferDto transfer = new TransferDto();
        when(transferService.getTransferById(id)).thenReturn(transfer);

        mockMvc.perform(get("/api/transfers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldExecuteTransfer() throws Exception {
        TransferRequest request = new TransferRequest();
        doNothing().when(transferService).executeTransfer(any(TransferRequest.class));

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer executed successfully"));
    }
}