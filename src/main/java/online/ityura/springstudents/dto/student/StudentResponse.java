package online.ityura.springstudents.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    String firstName;
    String secondName;
    String email;
    LocalDate birthDate;
    Integer age;
}


