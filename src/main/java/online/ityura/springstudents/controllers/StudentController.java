package online.ityura.springstudents.controllers;

import com.github.javafaker.Faker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/students")
@Tag(name = "students", description = "Операции со студентами")
public class StudentController {
    private final StudentServiceInterface studentServiceInterface;

    @GetMapping
    @Operation(summary = "Получить студентов постранично с сортировкой")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "studentsPageResponse",
                                    value = """
                                            {
                                              "content": [
                                                {
                                                  "id": 1,
                                                  "firstName": "Stepanie",
                                                  "secondName": "Bauch",
                                                  "email": "stepanie.bauch0@bernhard.net",
                                                  "birthDate": "1995-11-09",
                                                  "age": 30
                                                },
                                                {
                                                  "id": 2,
                                                  "firstName": "Walter",
                                                  "secondName": "Cummerata",
                                                  "email": "walter.cummerata1@macgyver.co",
                                                  "birthDate": "1988-01-21",
                                                  "age": 37
                                                },
                                                {
                                                  "id": 3,
                                                  "firstName": "Teodoro",
                                                  "secondName": "Fritsch",
                                                  "email": "teodoro.fritsch2@kozey.info",
                                                  "birthDate": "2000-06-06",
                                                  "age": 25
                                                },
                                                {
                                                  "id": 4,
                                                  "firstName": "Tamekia",
                                                  "secondName": "Monahan",
                                                  "email": "tamekia.monahan3@kertzmann.io",
                                                  "birthDate": "1996-02-20",
                                                  "age": 29
                                                },
                                                {
                                                  "id": 5,
                                                  "firstName": "Charlyn",
                                                  "secondName": "Hagenes",
                                                  "email": "charlyn.hagenes4@carroll.com",
                                                  "birthDate": "1953-01-08",
                                                  "age": 72
                                                }
                                              ],
                                              "empty": false,
                                              "first": true,
                                              "last": false,
                                              "number": 0,
                                              "numberOfElements": 5,
                                              "pageable": {
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 5,
                                                "paged": true,
                                                "sort": {
                                                  "empty": false,
                                                  "sorted": true,
                                                  "unsorted": false
                                                },
                                                "unpaged": false
                                              },
                                              "size": 5,
                                              "sort": {
                                                "empty": false,
                                                "sorted": true,
                                                "unsorted": false
                                              },
                                              "totalElements": 50,
                                              "totalPages": 10
                                            }
                                            """
                            )))
    })
    public ResponseEntity<?> getStudents(
            @ParameterObject
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        try {
            Page<Student> page = studentServiceInterface.getStudents(pageable);
            return ResponseEntity.ok(page);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(ex.getMessage()));
        }
    }

    @Deprecated
    @GetMapping(path = "/page")
    @Operation(summary = "Получить студентов постранично с сортировкой", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "studentsPageResponse",
                                    value = """
                                            									{
                                              "content": [
                                                {
                                                  "id": 1,
                                                  "firstName": "Stepanie",
                                                  "secondName": "Bauch",
                                                  "email": "stepanie.bauch0@bernhard.net",
                                                  "birthDate": "1995-11-09",
                                                  "age": 30
                                                },
                                                {
                                                  "id": 2,
                                                  "firstName": "Walter",
                                                  "secondName": "Cummerata",
                                                  "email": "walter.cummerata1@macgyver.co",
                                                  "birthDate": "1988-01-21",
                                                  "age": 37
                                                },
                                                {
                                                  "id": 3,
                                                  "firstName": "Teodoro",
                                                  "secondName": "Fritsch",
                                                  "email": "teodoro.fritsch2@kozey.info",
                                                  "birthDate": "2000-06-06",
                                                  "age": 25
                                                },
                                                {
                                                  "id": 4,
                                                  "firstName": "Tamekia",
                                                  "secondName": "Monahan",
                                                  "email": "tamekia.monahan3@kertzmann.io",
                                                  "birthDate": "1996-02-20",
                                                  "age": 29
                                                },
                                                {
                                                  "id": 5,
                                                  "firstName": "Charlyn",
                                                  "secondName": "Hagenes",
                                                  "email": "charlyn.hagenes4@carroll.com",
                                                  "birthDate": "1953-01-08",
                                                  "age": 72
                                                }
                                              ],
                                              "empty": false,
                                              "first": true,
                                              "last": false,
                                              "number": 0,
                                              "numberOfElements": 5,
                                              "pageable": {
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 5,
                                                "paged": true,
                                                "sort": {
                                                  "empty": false,
                                                  "sorted": true,
                                                  "unsorted": false
                                                },
                                                "unpaged": false
                                              },
                                              "size": 5,
                                              "sort": {
                                                "empty": false,
                                                "sorted": true,
                                                "unsorted": false
                                              },
                                              "totalElements": 50,
                                              "totalPages": 10
                                            }
                                            """
                            )))
    })
    public ResponseEntity<?> getStudentsPage(
            @ParameterObject
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        try {
            Page<Student> page = studentServiceInterface.getStudents(pageable);
            return ResponseEntity.ok(page);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(ex.getMessage()));
        }
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
    public ResponseEntity<MessageResponse> createStudent(@org.springframework.web.bind.annotation.RequestBody Student student) {
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
                                                                @PathVariable String email) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/generate/{count}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Сгенерировать случайных студентов (1–50)  (только для админа)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студенты сгенерированы",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(name = "generateSuccessResponse",
                                    value = """
                                            {
                                              "message": "Сгенерировано 10 из 10 студентов"
                                            }
                                            """)
                    )),
            @ApiResponse(responseCode = "400", description = "Некорректное число для генерации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(name = "generateBadRequestResponse",
                                    value = """
                                            {
                                              "message": "Количество студентов должно быть в диапазоне 1–50"
                                            }
                                            """)
                    ))
    })
    public ResponseEntity<MessageResponse> generateStudents(
            @Parameter(description = "Количество создаваемых студентов (1–50)", example = "10")
            @PathVariable int count
    ) {
        if (count < 1 || count > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Количество студентов должно быть в диапазоне 1–50"));
        }

        Faker faker = new Faker();
        int created = 0;
        for (int i = 0; i < count; i++) {
            String firstName = faker.name().firstName();
            String secondName = faker.name().lastName();
            // Снижаем шанс коллизий email добавляя индекс и случайные цифры
            String emailLocalPart = (firstName + "." + secondName).toLowerCase();
            String emailDomainPart = faker.internet().domainName();
            String email = emailLocalPart + i + "@" + emailDomainPart;

            var birthdayDate = faker.date().birthday(18, 80)
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Student student = new Student();
            student.setFirstName(firstName);
            student.setSecondName(secondName);
            student.setEmail(email);
            student.setBirthDate(birthdayDate);

            try {
                studentServiceInterface.createStudent(student);
                created++;
            } catch (DataIntegrityViolationException ignored) {
                // пропускаем дубликаты email из-за уникального ограничения
            }
        }

        String message = "Сгенерировано " + created + " из " + count + " студентов";
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
