package pl.edu.pjwstk.dusigrosz.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.pl.PESEL;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visors")
@Getter
@Setter
@NoArgsConstructor
public class Visor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @PESEL
    @Column(unique = true, nullable = false)
    private String pesel;
    private String phoneNumber;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "visor", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

}
