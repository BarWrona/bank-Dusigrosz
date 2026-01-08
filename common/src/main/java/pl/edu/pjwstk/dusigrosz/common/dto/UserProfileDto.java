package pl.edu.pjwstk.dusigrosz.common.dto;

import lombok.*;

@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private long userId;
    private boolean twoFactorEnabled;
    private boolean additionalDataCollecting;
}
