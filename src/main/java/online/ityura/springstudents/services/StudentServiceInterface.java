package online.ityura.springstudents.services;

import online.ityura.springstudents.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentServiceInterface {
    List<Student> getAllStudents();

    Page<Student> getStudents(Pageable pageable);

    void createStudent(Student student);

    boolean deleteStudentByEmail(String email);

    boolean updateStudentByEmail(Student student, String email);
}
