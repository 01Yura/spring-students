package online.ityura.springstudents.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.ityura.springstudents.models.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSuccessResponse {
        private String login;
        private String email;
        private Role role;
        private String message;
}


