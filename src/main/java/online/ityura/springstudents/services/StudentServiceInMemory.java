package online.ityura.springstudents.services;

import lombok.AllArgsConstructor;
import online.ityura.springstudents.models.Student;
import online.ityura.springstudents.repositories.InMemoryStudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class StudentServiceInMemory implements StudentServiceInterface {
    private final InMemoryStudentRepository inMemoryStudentRepository;

    public List<Student> getAllStudents() {
        return inMemoryStudentRepository.getInMemoryStudentRepositoryList();
    }

    @Override
    public Page<Student> getStudents(Pageable pageable) {
        List<Student> source = new ArrayList<>(inMemoryStudentRepository.getInMemoryStudentRepositoryList());

        // Сортировка с белым списком и стабильным тай-брейком по id
        Sort sort = pageable.getSort();
        source.sort(buildComparator(sort));

        int page = Math.max(0, pageable.getPageNumber());
        int size = Math.max(1, pageable.getPageSize());
        int from = Math.min(page * size, source.size());
        int to = Math.min(from + size, source.size());
        List<Student> content = source.subList(from, to);

        Pageable pageRequest = PageRequest.of(page, size, sort.isUnsorted() ? Sort.by("id").ascending() : sort);
        return new PageImpl<>(content, pageRequest, source.size());
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

    private Comparator<Student> buildComparator(Sort requestedSort) {
        Set<String> allowed = Set.of("id", "firstName", "secondName", "email", "birthDate");

        Comparator<Student> comparator = Comparator.comparing(Student::getId, Comparator.nullsLast(Long::compareTo));

        if (requestedSort != null && requestedSort.isSorted()) {
            boolean hasId = false;
            for (Sort.Order order : requestedSort) {
                String property = order.getProperty();
                if (!allowed.contains(property)) {
                    throw new IllegalArgumentException("Недопустимое поле сортировки: " + property);
                }
                Comparator<Student> next;
                switch (property) {
                    case "firstName" -> next = Comparator.comparing(Student::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase));
                    case "secondName" -> next = Comparator.comparing(Student::getSecondName, Comparator.nullsLast(String::compareToIgnoreCase));
                    case "email" -> next = Comparator.comparing(Student::getEmail, Comparator.nullsLast(String::compareToIgnoreCase));
                    case "birthDate" -> next = Comparator.comparing(Student::getBirthDate, Comparator.nullsLast(java.time.LocalDate::compareTo));
                    case "id" -> {
                        next = Comparator.comparing(Student::getId, Comparator.nullsLast(Long::compareTo));
                        hasId = true;
                    }
                    default -> throw new IllegalArgumentException("Недопустимое поле сортировки: " + property);
                }
                if (order.getDirection() == Sort.Direction.DESC) {
                    next = next.reversed();
                }
                comparator = comparator.thenComparing(next);
            }
            if (!hasId) {
                comparator = comparator.thenComparing(Comparator.comparing(Student::getId, Comparator.nullsLast(Long::compareTo)));
            }
        }
        return comparator;
    }
}
