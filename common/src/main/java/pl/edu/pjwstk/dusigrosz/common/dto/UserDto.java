package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    @PESEL
    private String pesel;
    private String phoneNumber;
    private String username;
    private String supervisorName;
    private String supervisorPhoneNumber;
    private UserProfileDto profile;
}
