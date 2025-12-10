package online.ityura.springstudents.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String secondName;
    @Column(unique = true)
    private String email;
    private LocalDate birthDate;
    @Transient // Hibernate/JPA игнорирует поле. Для него не будет колонки в таблице
    private Integer age;

    public Integer getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
