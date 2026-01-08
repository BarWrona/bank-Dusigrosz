package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {

    private Long id;
    private String name;
    private String code;
    private BigDecimal exchangeRate;
}
