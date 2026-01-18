package pl.edu.pjwstk.dusigrosz.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjwstk.dusigrosz.common.dto.CurrencyDto;
import pl.edu.pjwstk.dusigrosz.service.service.CurrencyService;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
@AutoConfigureMockMvc(addFilters = false)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrencyService currencyService;

    @Test
    void shouldGetAllCurrencies() throws Exception {
        CurrencyDto currency = new CurrencyDto();
        when(currencyService.getAllCurrencies()).thenReturn(List.of(currency));

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetCurrencyByCode() throws Exception {
        String code = "PLN";
        CurrencyDto currency = new CurrencyDto();
        when(currencyService.getCurrencyByCode(code)).thenReturn(currency);

        mockMvc.perform(get("/api/currencies/{code}", code))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldUpdateExchangeRates() throws Exception {
        doNothing().when(currencyService).updateExchangeRates();

        mockMvc.perform(post("/api/currencies/update-rates"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rates updated"));
    }
}