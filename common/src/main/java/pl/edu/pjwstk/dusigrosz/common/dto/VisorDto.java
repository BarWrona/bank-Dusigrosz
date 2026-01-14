package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VisorDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    @PESEL
    private String pesel;
    private List<Long> assignedUserIds;

}
