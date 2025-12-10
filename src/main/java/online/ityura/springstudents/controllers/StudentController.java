package online.ityura.springstudents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	@Operation(summary = "Получить список всех студентов")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Успешно",
					content = @Content(mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = Student.class)),
							examples = @ExampleObject(name = "students",
									value = """
									[
									  {
									    "id": 1,
									    "firstName": "John",
									    "secondName": "Doe",
									    "email": "john.doe@example.com",
									    "birthDate": "2000-01-15",
									    "age": 25
									  }
									]
									"""
							)
					))
	})
    public List<Student> getAllStudents(){
        return studentServiceInterface.getAllStudents();
    }

    @PostMapping
	@Operation(summary = "Создать нового студента")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = Student.class),
					examples = @ExampleObject(name = "createStudentRequest",
							value = """
							{
							  "firstName": "John",
							  "secondName": "Doe",
							  "email": "john.doe@example.com",
							  "birthDate": "2000-01-15"
							}
							""")
			))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Студент создан",
					content = @Content(mediaType = "text/plain",
							examples = @ExampleObject(name = "createStudentResponse",
									value = """
									Student with name and surname: John Doe has been created
									""")))
	})
    public String createStudent(@RequestBody Student student){
        studentServiceInterface.createStudent(student);
        return  "Student with name and surname: " + student.getFirstName() + " " + student.getSecondName() + " has " +
                "been created";
    }

    @DeleteMapping(path = "/email/{email}")
	@Operation(summary = "Удалить студента по email")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Студент удален",
					content = @Content(mediaType = "text/plain",
							examples = @ExampleObject(name = "deleteResponse",
									value = """
									Student with email: john.doe@example.com has been deleted SUCCESSFULLY
									""")))
	})
	public String deleteStudentByEmail(@Parameter(description = "Email студента", example = "john.doe@example.com")
									   @PathVariable String email){
        studentServiceInterface.deleteStudentByEmail(email);
        return "Student with email: " + email + " has been deleted SUCCESSFULLY";
    }

    @PutMapping(path = "/{email}")
	@Operation(summary = "Обновить данные студента по email")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = Student.class),
					examples = @ExampleObject(name = "updateStudentRequest",
							value = """
							{
							  "firstName": "Jane",
							  "secondName": "Doe",
							  "email": "jane.doe@example.com",
							  "birthDate": "1999-05-20"
							}
							""")
			))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Успешно",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Student.class),
							examples = @ExampleObject(name = "updateStudentResponse",
									value = """
									{
									  "id": 1,
									  "firstName": "Jane",
									  "secondName": "Doe",
									  "email": "jane.doe@example.com",
									  "birthDate": "1999-05-20",
									  "age": 26
									}
									""")
			)),
			@ApiResponse(responseCode = "404", description = "Студент не найден")
	})
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
