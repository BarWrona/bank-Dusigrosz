package pl.edu.pjwstk.bankingservice.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TransferDto {

    private long id;
    private String senderIban;
    private String receiverIban;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal exchangeRate;
    private LocalDateTime createdAt;

}
