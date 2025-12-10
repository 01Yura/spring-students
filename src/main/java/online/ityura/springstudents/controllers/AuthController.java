package online.ityura.springstudents.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import online.ityura.springstudents.models.AppUser;
import online.ityura.springstudents.models.Role;
import online.ityura.springstudents.repositories.AppUserRepository;
import online.ityura.springstudents.security.JwtService;
import online.ityura.springstudents.dto.ErrorResponse;
import online.ityura.springstudents.dto.auth.RegisterRequest;
import online.ityura.springstudents.dto.auth.LoginRequest;
import online.ityura.springstudents.dto.auth.RefreshRequest;
import online.ityura.springstudents.dto.auth.AuthResponse;
import online.ityura.springstudents.dto.auth.RegisterSuccessResponse;
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
		if (appUserRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.badRequest().body(new ErrorResponse("User with this email already exists"));
		}
		AppUser user = new AppUser();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
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
			@ApiResponse(responseCode = "401", description = "Неверные учетные данные",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(name = "invalidCredentialsResponse",
									value = """
									{
									  "message": "Invalid email or password"
									}
									""")))
	})
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		try {
				authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
			);
		} catch (BadCredentialsException ex) {
				return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
		}
		AppUser user = appUserRepository.findByEmail(request.getEmail()).orElseThrow();
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
				@ApiResponse(responseCode = "401", description = "Недействительный refresh token",
						content = @Content(mediaType = "application/json",
								schema = @Schema(implementation = ErrorResponse.class),
								examples = @ExampleObject(name = "invalidRefreshTokenResponse",
										value = """
										{
										  "message": "Invalid refresh token"
										}
										""")))
	})
	public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest request) {
		String email;
		try {
			email = jwtService.extractEmail(request.getRefreshToken());
		} catch (Exception e) {
			return ResponseEntity.status(401).body(new ErrorResponse("Invalid refresh token"));
		}
		if (email == null || jwtService.isTokenExpired(request.getRefreshToken())) {
			return ResponseEntity.status(401).body(new ErrorResponse("Invalid refresh token"));
		}
		AppUser user = appUserRepository.findByEmail(email).orElse(null);
		if (user == null) {
			return ResponseEntity.status(401).body(new ErrorResponse("Invalid refresh token"));
		}
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);
		return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, 300));
	}

}


