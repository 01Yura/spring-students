package online.ityura.springstudents.exception;

import online.ityura.springstudents.dto.MessageResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MessageResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse("Validation error");
		return ResponseEntity.badRequest().body(new MessageResponse(message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<MessageResponse> handleConstraintViolation(ConstraintViolationException ex) {
		String message = ex.getConstraintViolations()
				.stream()
				.map(cv -> cv.getMessage())
				.filter(Objects::nonNull)
				.findFirst()
				.orElse("Validation error");
		return ResponseEntity.badRequest().body(new MessageResponse(message));
	}
}


