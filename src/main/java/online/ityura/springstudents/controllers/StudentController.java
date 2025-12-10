package online.ityura.springstudents.controllers;

import lombok.AllArgsConstructor;
import online.ityura.springstudents.models.Student;
import online.ityura.springstudents.services.StudentServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/students")
public class StudentController {
    private final StudentServiceInterface studentServiceInterface;

    @GetMapping
    public List<Student> getAllStudents(){
        return studentServiceInterface.getAllStudents();
    }

    @PostMapping
    public String createStudent(@RequestBody Student student){
        studentServiceInterface.createStudent(student);
        return  "Student with name and surname: " + student.getFirstName() + " " + student.getSecondName() + " has " +
                "been created";
    }

    @DeleteMapping(path = "/email/{email}")
    public String deleteStudentByEmail(@PathVariable String email){
        studentServiceInterface.deleteStudentByEmail(email);
        return "Student with email: " + email + " has been deleted SUCCESSFULLY";
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<Student> updateStudentByEmail(@RequestBody Student student,
                                                        @PathVariable String email) {
        boolean updated = studentServiceInterface.updateStudentByEmail(student, email);
        if (updated) {
            return ResponseEntity.ok(student);           // 200 OK
        } else {
            return ResponseEntity.notFound().build();    // 404 Not Found
        }
    }
}
