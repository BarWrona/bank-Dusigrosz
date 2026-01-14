package pl.edu.pjwstk.dusigrosz.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjwstk.dusigrosz.common.dto.AccountDto;
import pl.edu.pjwstk.dusigrosz.service.service.AccountService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllAccounts() throws Exception {
        AccountDto account = new AccountDto();
        when(accountService.findAll()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldCreateAccount() throws Exception {
        AccountDto accountDto = new AccountDto();
        when(accountService.create(any(AccountDto.class))).thenReturn(accountDto);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetAccountById() throws Exception {
        Long id = 1L;
        AccountDto account = new AccountDto();
        when(accountService.getById(id)).thenReturn(account);

        mockMvc.perform(get("/api/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldDeleteAccount() throws Exception {
        Long id = 1L;
        doNothing().when(accountService).delete(id);

        mockMvc.perform(delete("/api/accounts/{id}", id))
                .andExpect(status().isNoContent());
    }
}