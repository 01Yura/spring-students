package online.ityura.springstudents.services;

import online.ityura.springstudents.models.Student;

import java.util.List;

public interface StudentServiceInterface {
    List<Student> getAllStudents();

    void createStudent(Student student);

    void deleteStudentByEmail(String email);

    boolean updateStudentByEmail(Student student, String email);
}
