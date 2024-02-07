package com.noCompany.BackendStableDiffusionWebApp;

import com.noCompany.BackendStableDiffusionWebApp.domain.Model;
import com.noCompany.BackendStableDiffusionWebApp.domain.RefreshToken;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.ModelRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
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

	@Autowired
	public BackendStableDiffusionWebAppApplication(UserRepository userRepository, PasswordEncoder passwordEncoder,
												   RefreshTokenRepository refreshTokenRepository,
												   ModelRepository modelRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.refreshTokenRepository = refreshTokenRepository;
		this.modelRepository = modelRepository;
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
				.password(passwordEncoder.encode(passwordEncoder.encode("password")))
				.provider(Provider.self)
				.credits(100L)
				.roles(Set.of(Role.USER, Role.ADMIN))
				.build();

		RefreshToken token = new RefreshToken();
		token.setRefreshToken("secure");
		token.setExpiryDate(LocalDateTime.now());
		token.setUser(user);


		if (!userRepository.findByUsername(user.getUsername()).isPresent())
			userRepository.save(user);

		if(!refreshTokenRepository.findByRefreshToken("secure").isPresent())
			refreshTokenRepository.save(token);


		Model testModel = Model.builder()
				.id(1L)
				.name("Stable-Diffusion-2-1")
				.inferenceUrl("https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-2-1")
				.build();
		if(!modelRepository.existsById(1L)) {
			modelRepository.save(testModel);
		}
	}
}
