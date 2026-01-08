package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String iban;
    private Long currencyId;
    private BigDecimal balance;
    private Set<Long> userIds;
}
