package online.ityura.springstudents.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private int expiresInSeconds;
}


