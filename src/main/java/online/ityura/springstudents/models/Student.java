package online.ityura.springstudents.models;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(example = "1")
	private Long id;
	@Schema(example = "John")
	private String firstName;
	@Schema(example = "Doe")
	private String secondName;
    @Column(unique = true)
	@Schema(example = "john.doe@example.com")
	private String email;
	@Schema(example = "2000-01-15", type = "string", format = "date")
	private LocalDate birthDate;
    @Transient
	@Schema(description = "Calculated from birthDate", accessMode = Schema.AccessMode.READ_ONLY, example = "25")
	private Integer age;

    public Integer getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
