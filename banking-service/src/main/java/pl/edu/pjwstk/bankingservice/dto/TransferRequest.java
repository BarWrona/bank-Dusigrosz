package pl.edu.pjwstk.bankingservice.dto;

import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class TransferRequest {
    private String senderIban;
    private String receiverIban;
    private BigDecimal amountSent;
}
