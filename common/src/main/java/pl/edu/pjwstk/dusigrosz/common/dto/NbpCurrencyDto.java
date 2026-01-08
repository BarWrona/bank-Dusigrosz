package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class NbpCurrencyDto {
    private String currency;
    private String code;
    private Double mid;
}
