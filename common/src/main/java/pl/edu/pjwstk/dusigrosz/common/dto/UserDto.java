package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String pesel;
    private String phoneNumber;
    private String username;
}
