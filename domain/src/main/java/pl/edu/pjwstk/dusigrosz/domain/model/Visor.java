package pl.edu.pjwstk.dusigrosz.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String pesel;
    private String phoneNumber;

    @OneToMany(mappedBy = "visor", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();


}


