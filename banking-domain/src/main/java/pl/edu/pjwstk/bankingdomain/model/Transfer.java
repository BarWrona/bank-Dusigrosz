package pl.edu.pjwstk.bankingdomain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
@Getter
@Setter
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_iban", referencedColumnName = "iban")
    private Account senderIban;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_iban", referencedColumnName = "iban")
    private Account receiver;
    @Column(precision = 10, scale = 2)
    private BigDecimal amountSent;
    @Column(precision = 10, scale = 2)
    private BigDecimal amountReceived;
    @Column(precision = 10, scale = 5)
    private BigDecimal exchangeRate;
    @CreationTimestamp
    private LocalDateTime createdAt;

}
