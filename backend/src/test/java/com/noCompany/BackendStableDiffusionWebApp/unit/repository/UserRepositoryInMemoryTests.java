package com.noCompany.BackendStableDiffusionWebApp.unit.repository;

import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryInMemoryTests {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void initUser() {
        user = User.builder()
                .email("test@gmail.com")
                .username("testuser")
                .password("password")
                .provider(Provider.self)
                .roles(Set.of(Role.ADMIN))
                .build();
    }

    @AfterEach
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Test
    public void SavedUser_ReturnsSavedUser() {
        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedUser.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    public void SavedUser_FindUserByUsername() {
        userRepository.save(user);
        Optional<User> savedUserOpt = userRepository.findByUsername(user.getUsername());

        Assertions.assertThat(savedUserOpt).isNotEmpty();
        User savedUser = savedUserOpt.get();

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void SavedUser_FindUserByEmail() {
        userRepository.save(user);

        Optional<User> savedUserOpt = userRepository.findByEmail(user.getEmail());

        Assertions.assertThat(savedUserOpt).isNotEmpty();
        User savedUser = savedUserOpt.get();

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void SavedUserExistsByEmail() {
        userRepository.save(user);
        Boolean userExists = userRepository.existsByEmail(user.getEmail());
        Assertions.assertThat(userExists).isEqualTo(true);
    }
}
