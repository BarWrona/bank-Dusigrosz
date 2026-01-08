package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.*;

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
    private String pesel;
    private List<Long> assignedUserIds;

}
