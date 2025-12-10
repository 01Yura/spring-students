package online.ityura.springstudents.repositories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import online.ityura.springstudents.models.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Getter
@AllArgsConstructor
public class InMemoryStudentRepository {
    private final List<Student> inMemoryStudentRepositoryList = new ArrayList<>();
}
