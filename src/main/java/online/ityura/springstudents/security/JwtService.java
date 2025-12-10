package online.ityura.springstudents.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import online.ityura.springstudents.models.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {
	private final Key signingKey;

	// 5 minutes for access, 24 hours for refresh (in seconds)
	private static final long ACCESS_TTL_SECONDS = 5 * 60;
	private static final long REFRESH_TTL_SECONDS = 24 * 60 * 60;

	public JwtService(@Value("${jwt.secret:please-change-this-secret-please-change-this-secret}") String secret) {
		String keyString = ensureMinLength(secret, 32);
		byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
		this.signingKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateAccessToken(AppUser user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.getRole().name());
		return buildToken(claims, user.getEmail(), ACCESS_TTL_SECONDS);
	}

	public String generateRefreshToken(AppUser user) {
		return buildToken(new HashMap<>(), user.getEmail(), REFRESH_TTL_SECONDS);
	}

	public boolean isTokenValid(String token, String expectedEmail) {
		final String email = extractEmail(token);
		return email != null && email.equalsIgnoreCase(expectedEmail) && !isTokenExpired(token);
	}

	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(signingKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private String buildToken(Map<String, Object> claims, String subject, long ttlSeconds) {
		Instant now = Instant.now();
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
				.signWith(signingKey, SignatureAlgorithm.HS256)
				.compact();
	}

	private static String ensureMinLength(String input, int minLen) {
		if (input == null) return "default-secret-value-that-is-long-enough-123456";
		String s = input;
		while (s.length() < minLen) {
			s = s + input;
		}
		return s;
	}
}


