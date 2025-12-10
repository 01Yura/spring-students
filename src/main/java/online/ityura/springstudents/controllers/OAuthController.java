package online.ityura.springstudents.controllers;

import lombok.AllArgsConstructor;
import online.ityura.springstudents.models.AppUser;
import online.ityura.springstudents.repositories.AppUserRepository;
import online.ityura.springstudents.security.JwtService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {
	private final AuthenticationManager authenticationManager;
	private final AppUserRepository appUserRepository;
	private final JwtService jwtService;

	@PostMapping(
			value = "/token",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> token(@RequestParam MultiValueMap<String, String> form) {
		String grantType = valueOrEmpty(form.getFirst("grant_type"));
		if (!"password".equals(grantType)) {
			return ResponseEntity.badRequest().body(error("unsupported_grant_type", "Only 'password' grant is supported"));
		}
		String username = valueOrEmpty(form.getFirst("username"));
		String password = valueOrEmpty(form.getFirst("password"));
		if (username.isBlank() || password.isBlank()) {
			return ResponseEntity.badRequest().body(error("invalid_request", "username and password are required"));
		}

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password)
			);
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(401).body(error("invalid_grant", "Bad credentials"));
		}

		AppUser user = appUserRepository.findByEmail(username).orElse(null);
		if (user == null) {
			return ResponseEntity.status(401).body(error("invalid_grant", "User not found"));
		}

		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);

		Map<String, Object> response = new HashMap<>();
		response.put("access_token", accessToken);
		response.put("token_type", "Bearer");
		response.put("expires_in", 300);
		response.put("refresh_token", refreshToken);
		return ResponseEntity.ok(response);
	}

	private static Map<String, Object> error(String code, String description) {
		Map<String, Object> err = new HashMap<>();
		err.put("error", code);
		err.put("error_description", description);
		return err;
	}

	private static String valueOrEmpty(String s) {
		return s == null ? "" : s;
	}
}


