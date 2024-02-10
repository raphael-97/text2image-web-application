package com.noCompany.BackendStableDiffusionWebApp;

import com.noCompany.BackendStableDiffusionWebApp.domain.RefreshToken;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.ModelRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@SpringBootApplication
public class BackendStableDiffusionWebAppApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private final RefreshTokenRepository refreshTokenRepository;

	private final ModelRepository modelRepository;

	private final ImageStorageService imageStorageService;

	@Autowired
	public BackendStableDiffusionWebAppApplication(UserRepository userRepository, PasswordEncoder passwordEncoder,
												   RefreshTokenRepository refreshTokenRepository,
												   ModelRepository modelRepository,
												   ImageStorageService imageStorageService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.refreshTokenRepository = refreshTokenRepository;
		this.modelRepository = modelRepository;
		this.imageStorageService = imageStorageService;
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendStableDiffusionWebAppApplication.class, args);
	}

	// Just for testing :)
	@Override
	public void run(String... args) throws Exception {

		User user = User.builder()
				.username("testuser")
				.email("testuser@gmail.com")
				.password(passwordEncoder.encode("password"))
				.provider(Provider.self)
				.credits(100L)
				.roles(Set.of(Role.USER, Role.ADMIN))
				.build();

		RefreshToken token = new RefreshToken();
		token.setRefreshToken("secure");
		token.setExpiryDate(LocalDateTime.now());
		token.setUser(user);

		userRepository.save(user);
		refreshTokenRepository.save(token);
	}
}
