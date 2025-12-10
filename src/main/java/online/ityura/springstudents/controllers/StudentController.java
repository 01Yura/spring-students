package online.ityura.springstudents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import online.ityura.springstudents.dto.MessageResponse;
import online.ityura.springstudents.dto.student.StudentResponse;
import online.ityura.springstudents.models.Student;
import online.ityura.springstudents.services.StudentServiceInterface;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/students")
@Tag(name = "students", description = "Операции со студентами")
public class StudentController {
    private final StudentServiceInterface studentServiceInterface;

    @GetMapping
	@Operation(summary = "Получить список всех студентов")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Успешно",
					content = @Content(mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = Student.class)),
							examples = @ExampleObject(name = "studentsList",
									value = """
									[
									  {
									    "id": 1,
									    "firstName": "John",
									    "secondName": "Doe",
									    "email": "john.doe@example.com",
									    "birthDate": "2000-01-15",
									    "age": 25
									  },
									  {
									    "id": 2,
									    "firstName": "Jane",
									    "secondName": "Smith",
									    "email": "jane.smith@example.com",
									    "birthDate": "1999-05-20",
									    "age": 26
									  }
									]
									""")))
	})
    public List<Student> getAllStudents(){
        return studentServiceInterface.getAllStudents();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MessageResponse.class),
							examples = @ExampleObject(name = "createResponse",
									value = """
									{
									  "message": "Student with name and surname: John Doe has been created"
									}
									"""
							))),
			@ApiResponse(responseCode = "409", description = "Студент с таким email уже существует",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MessageResponse.class),
							examples = @ExampleObject(name = "emailExistsResponse",
									value = """
									{
									  "message": "Student with this email already exists"
									}
									""")))
	})
    public ResponseEntity<MessageResponse> createStudent(@org.springframework.web.bind.annotation.RequestBody Student student){
        try {
            studentServiceInterface.createStudent(student);
            String message = "Student with name and surname: " + student.getFirstName() + " " + student.getSecondName() + " has been created";
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Student with this email already exists"));
        }
    }

    @DeleteMapping(path = "/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Удалить студента по email (только для админа)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Студент удален",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MessageResponse.class),
							examples = @ExampleObject(name = "deleteResponse",
									value = """
									{
									  "message": "Student with email: john.doe@example.com has been deleted SUCCESSFULLY"
									}
									""")))
			,
			@ApiResponse(responseCode = "404", description = "Студент не найден",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MessageResponse.class),
							examples = @ExampleObject(name = "notFoundResponse",
									value = """
									{
									  "message": "Студент с email: john.doe@example.com не найден"
									}
									""")))
	})
	public ResponseEntity<MessageResponse> deleteStudentByEmail(@Parameter(description = "Email студента", example = "john.doe@example.com")
									   @PathVariable String email){
        boolean deleted = studentServiceInterface.deleteStudentByEmail(email);
        if (deleted) {
            String message = "Student with email: " + email + " has been deleted SUCCESSFULLY";
            return ResponseEntity.ok(new MessageResponse(message));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Студент с email: " + email + " не найден"));
        }
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
							schema = @Schema(implementation = StudentResponse.class),
							examples = @ExampleObject(name = "updateStudentResponse",
									value = """
									{
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
    public ResponseEntity<?> updateStudentByEmail(@org.springframework.web.bind.annotation.RequestBody Student student,
                                                        @PathVariable String email) {
        boolean updated = studentServiceInterface.updateStudentByEmail(student, email);
        if (updated) {
            StudentResponse response = new StudentResponse(
					student.getFirstName(),
					student.getSecondName(),
					student.getEmail(),
					student.getBirthDate(),
					student.getAge()
			);
            return ResponseEntity.ok(response);           // 200 OK without id
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Студент с email: " + email + " не найден"));    // 404 Not Found with JSON message
        }
    }
}
