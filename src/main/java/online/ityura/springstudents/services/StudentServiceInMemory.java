package online.ityura.springstudents.services;

import lombok.AllArgsConstructor;
import online.ityura.springstudents.models.Student;
import online.ityura.springstudents.repositories.InMemoryStudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StudentServiceInMemory implements StudentServiceInterface {
    private final InMemoryStudentRepository inMemoryStudentRepository;

    public List<Student> getAllStudents() {
        return inMemoryStudentRepository.getInMemoryStudentRepositoryList();
    }

    public void createStudent(Student student) {
        inMemoryStudentRepository.getInMemoryStudentRepositoryList().add(student);
    }

    public boolean deleteStudentByEmail(String email) {
        return inMemoryStudentRepository.getInMemoryStudentRepositoryList()
                .removeIf(student -> student.getEmail().equals(email));
    }

    public boolean updateStudentByEmail(Student student, String email) {
        List<Student> list = inMemoryStudentRepository.getInMemoryStudentRepositoryList();
        for (int i = 0; i < list.size(); i++) {
            Student current = list.get(i);
            if (current.getEmail().equals(email)) {
                list.set(i, student);
                return true; // нашли и обновили
            }
        }
        return false; // не нашли
    }
}
