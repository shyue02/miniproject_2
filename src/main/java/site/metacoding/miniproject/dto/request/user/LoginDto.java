package site.metacoding.miniproject.dto.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginDto {
    private String username;
    private String password;
}
