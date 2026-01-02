package pl.edu.pjwstk.bankingdomain.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @Column(length = 34)
    private String iban;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal balance;

    @ManyToMany(mappedBy = "accounts")
    private Set<User> users = new HashSet<>();
}
