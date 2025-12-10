package online.ityura.springstudents.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import online.ityura.springstudents.models.AppUser;
import online.ityura.springstudents.models.Role;
import online.ityura.springstudents.repositories.AppUserRepository;
import online.ityura.springstudents.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "auth", description = "Аутентификация и выдача JWT")
public class AuthController {
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@PostMapping("/register")
	@Operation(summary = "Регистрация нового пользователя")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(mediaType = "application/json",
					examples = @ExampleObject(name = "registerRequest",
							value = """
							{
							  "name": "John Doe",
							  "email": "john.doe@example.com",
							  "password": "P@ssw0rd!"
							}
							""")
			))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Зарегистрирован",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = RegisterSuccessResponse.class),
							examples = @ExampleObject(name = "registerSuccessResponse",
									value = """
									{
									  "login": "John Doe",
									  "email": "john.doe@example.com",
									  "role": "USER",
									  "message": "User successfully registered"
									}
									"""))),
			@ApiResponse(responseCode = "400", description = "Пользователь уже существует",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(name = "emailExistsResponse",
									value = """
									{
									  "message": "User with this email already exists"
									}
									""")))
	})
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
		if (appUserRepository.existsByEmail(request.email())) {
			return ResponseEntity.badRequest().body(new ErrorResponse("User with this email already exists"));
		}
		AppUser user = new AppUser();
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setRole(Role.USER);
		appUserRepository.save(user);

		return ResponseEntity.ok(new RegisterSuccessResponse(
				user.getName(),
				user.getEmail(),
				user.getRole(),
				"User successfully registered"
		));
	}

	@PostMapping("/login")
	@Operation(summary = "Логин по email и паролю")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(mediaType = "application/json",
					examples = @ExampleObject(name = "loginRequest",
							value = """
							{
							  "email": "john.doe@example.com",
							  "password": "P@ssw0rd!"
							}
							""")
			))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Успешный логин",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = AuthResponse.class),
							examples = @ExampleObject(name = "authResponse",
									value = """
									{
									  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
									  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
									  "expiresInSeconds": 300
									}
									"""))),
			@ApiResponse(responseCode = "401", description = "Неверные учетные данные")
	})
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.email(), request.password())
			);
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(401).build();
		}
		AppUser user = appUserRepository.findByEmail(request.email()).orElseThrow();
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);
		return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, 300));
	}

	@PostMapping("/refresh")
	@Operation(summary = "Обновление пары токенов по refreshToken")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(mediaType = "application/json",
					examples = @ExampleObject(name = "refreshRequest",
							value = """
							{
							  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
							}
							""")
			))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Токены обновлены",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = AuthResponse.class),
							examples = @ExampleObject(name = "authResponse",
									value = """
									{
									  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
									  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
									  "expiresInSeconds": 300
									}
									"""))),
			@ApiResponse(responseCode = "401", description = "Недействительный refresh token")
	})
	public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
		String email;
		try {
			email = jwtService.extractEmail(request.refreshToken());
		} catch (Exception e) {
			return ResponseEntity.status(401).build();
		}
		if (email == null || jwtService.isTokenExpired(request.refreshToken())) {
			return ResponseEntity.status(401).build();
		}
		AppUser user = appUserRepository.findByEmail(email).orElse(null);
		if (user == null) {
			return ResponseEntity.status(401).build();
		}
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);
		return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, 300));
	}

	public record RegisterRequest(
			@jakarta.validation.constraints.NotBlank String name,
			@jakarta.validation.constraints.Email @jakarta.validation.constraints.NotBlank String email,
			@jakarta.validation.constraints.NotBlank String password
	) {}

	public record LoginRequest(
			@jakarta.validation.constraints.Email @jakarta.validation.constraints.NotBlank String email,
			@jakarta.validation.constraints.NotBlank String password
	) {}

	public record RefreshRequest(
			@jakarta.validation.constraints.NotBlank String refreshToken
	) {}

	public record AuthResponse(
			String accessToken,
			String refreshToken,
			int expiresInSeconds
	) {}

	public record RegisterSuccessResponse(
			String login,
			String email,
			Role role,
			String message
	) {}

	public record ErrorResponse(
			String message
	) {}
}


