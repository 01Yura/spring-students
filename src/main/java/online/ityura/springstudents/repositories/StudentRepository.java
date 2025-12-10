package online.ityura.springstudents.repositories;

import online.ityura.springstudents.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// аннотацию @Repository не обязательно вешать так как она есть где то в JpaRepository
public interface StudentRepository extends JpaRepository<Student, Long> {
    long deleteByEmail(String email);

    Optional<Student> findByEmail(String email);
}
