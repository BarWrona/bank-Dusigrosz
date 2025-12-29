package pl.edu.pjwstk.bankingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private long userId;
    private boolean twoFactorEnabled;
    private boolean additionalDataCollecting;
}
