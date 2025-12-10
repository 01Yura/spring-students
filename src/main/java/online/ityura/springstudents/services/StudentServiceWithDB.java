package online.ityura.springstudents.services;

import lombok.AllArgsConstructor;
import online.ityura.springstudents.models.Student;
import online.ityura.springstudents.repositories.StudentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Primary
@Service
@AllArgsConstructor
public class StudentServiceWithDB implements StudentServiceInterface{
    private final StudentRepository studentRepository;

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public void createStudent(Student student) {
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public void deleteStudentByEmail(String email) {
        studentRepository.deleteByEmail(email);
    }

    @Override
    public boolean updateStudentByEmail(Student student, String email) {
        return studentRepository.findByEmail(email)
                .map(existing -> {
                    existing.setFirstName(student.getFirstName());
                    existing.setSecondName(student.getSecondName());
                    existing.setEmail(student.getEmail());
                    existing.setBirthDate(student.getBirthDate());
                    studentRepository.save(existing);
                    return true;
                })
                .orElse(false);
    }
}
