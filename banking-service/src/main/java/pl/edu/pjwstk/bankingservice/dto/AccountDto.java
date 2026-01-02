package pl.edu.pjwstk.bankingservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AccountDto {
    private String iban;
    private long currencyId;
    private BigDecimal balance;
    private Set<Long> userIds;
}
