package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class NbpTableDto{
    private String table;
    private String no;
    private String effectiveDate;
    private List<NbpCurrencyDto> rates;
}
