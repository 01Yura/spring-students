package online.ityura.springstudents.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;


    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=\\S+$)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=]).{8,}$",
            message = "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"
    )
    private String password;
}

