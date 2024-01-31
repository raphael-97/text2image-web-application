package com.noCompany.BackendStableDiffusionWebApp;

import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class BackendStableDiffusionWebAppApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public BackendStableDiffusionWebAppApplication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
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
				.credits(100L)
				.roles(Set.of("USER", "ADMIN"))
				.build();

		if (!userRepository.findByUsername(user.getUsername()).isPresent())
			userRepository.save(user);
	}
}
