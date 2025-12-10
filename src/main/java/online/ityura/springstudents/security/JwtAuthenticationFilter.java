package online.ityura.springstudents.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import online.ityura.springstudents.models.AppUser;
import online.ityura.springstudents.repositories.AppUserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final AppUserRepository appUserRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		final String token = authHeader.substring(7);
		String email;
		try {
			email = jwtService.extractEmail(token);
		} catch (Exception ex) {
			filterChain.doFilter(request, response);
			return;
		}
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			Optional<AppUser> userOpt = appUserRepository.findByEmail(email);
			if (userOpt.isPresent()) {
				AppUser user = userOpt.get();
				if (!jwtService.isTokenExpired(token) && jwtService.isTokenValid(token, user.getEmail())) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							user.getEmail(),
							null,
							List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
					);
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		}
		filterChain.doFilter(request, response);
	}
}


