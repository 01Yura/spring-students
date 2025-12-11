package online.ityura.springstudents.services;

import lombok.AllArgsConstructor;
import online.ityura.springstudents.models.Student;
import online.ityura.springstudents.repositories.StudentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Page<Student> getStudents(Pageable pageable) {
        Sort safeSort = buildSafeSort(pageable.getSort());
        Pageable safePageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
        return studentRepository.findAll(safePageRequest);
    }

    @Override
    public void createStudent(Student student) {
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public boolean deleteStudentByEmail(String email) {
        long deleted = studentRepository.deleteByEmail(email);
        return deleted > 0;
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

    private Sort buildSafeSort(Sort requestedSort) {
        // Белый список полей сущности, по которым разрешена сортировка
        Set<String> allowed = new HashSet<>(Set.of("id", "firstName", "secondName", "email", "birthDate"));

        // Если сортировка не задана — используем стабильную сортировку по id
        if (requestedSort == null || requestedSort.isUnsorted()) {
            return Sort.by(Sort.Order.asc("id"));
        }

        Sort result = Sort.unsorted();
        boolean hasId = false;
        for (Sort.Order order : requestedSort) {
            String property = order.getProperty();
            if (!allowed.contains(property)) {
                throw new IllegalArgumentException("Недопустимое поле сортировки: " + property);
            }
            if ("id".equals(property)) {
                hasId = true;
            }
            result = result.and(Sort.by(new Sort.Order(order.getDirection(), property)));
        }
        // Добавляем стабильный тай-брейк по id, если его нет
        if (!hasId) {
            result = result.and(Sort.by(Sort.Order.asc("id")));
        }
        return result;
    }
}
