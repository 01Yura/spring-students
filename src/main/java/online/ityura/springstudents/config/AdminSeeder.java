package online.ityura.springstudents.config;

import lombok.RequiredArgsConstructor;
import online.ityura.springstudents.models.AppUser;
import online.ityura.springstudents.models.Role;
import online.ityura.springstudents.repositories.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		// Seed default admin if absent
		final String adminEmail = "admin@local";
		if (!appUserRepository.existsByEmail(adminEmail)) {
			AppUser admin = new AppUser();
			admin.setName("admin");
			admin.setEmail(adminEmail);
			admin.setPasswordHash(passwordEncoder.encode("admin"));
			admin.setRole(Role.ADMIN);
			appUserRepository.save(admin);
		}
	}
}


