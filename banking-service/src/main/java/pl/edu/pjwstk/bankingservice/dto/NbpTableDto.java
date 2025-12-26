package pl.edu.pjwstk.bankingservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class NbpTableDto{
    private String table;
    private String no;
    private String effectiveDate;
    private List<CurrencyDto> rates;
}
