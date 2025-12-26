package pl.edu.pjwstk.bankingservice.dto;

import lombok.Data;

@Data
public class CurrencyDto {
    private  String currency;
    private String code;
    private Double mid;
}
