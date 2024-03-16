package com.noCompany.BackendStableDiffusionWebApp.unit.repository;

import com.noCompany.BackendStableDiffusionWebApp.domain.RefreshToken;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RefreshTokenRepositoryInMemoryTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    public void initUser() {
        user = User.builder()
                .email("test@gmail.com")
                .username("testuser")
                .password("password")
                .provider(Provider.self)
                .roles(Set.of(Role.ADMIN))
                .build();
        refreshToken = RefreshToken.builder()
                .refreshToken("refreshToken")
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(3600))
                .build();

        userRepository.save(user);
    }

    @AfterEach
    public void deleteAll() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void SaveRefreshToken_ReturnsSavedRefreshToken() {
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        Assertions.assertThat(savedRefreshToken).isNotNull();
        Assertions.assertThat(savedRefreshToken.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedRefreshToken.getUser()).isEqualTo(user);
        Assertions.assertThat(savedRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    public void SaveRefreshToken_FindByRefreshTokenReturnsSavedRefreshToken() {
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> savedRefreshTokenOpt = refreshTokenRepository
                .findByRefreshToken(refreshToken.getRefreshToken());

        Assertions.assertThat(savedRefreshTokenOpt).isNotEmpty();
        RefreshToken savedRefreshToken = savedRefreshTokenOpt.get();

        Assertions.assertThat(savedRefreshToken).isNotNull();
        Assertions.assertThat(savedRefreshToken.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    public void SaveRefreshToken_FindRefreshTokenByUserReturnsSavedRefreshToken() {
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> savedRefreshTokenOpt = refreshTokenRepository
                .findByUser(user);

        Assertions.assertThat(savedRefreshTokenOpt).isNotEmpty();
        RefreshToken savedRefreshToken = savedRefreshTokenOpt.get();

        Assertions.assertThat(savedRefreshToken).isNotNull();
        Assertions.assertThat(savedRefreshToken.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedRefreshToken).isEqualTo(refreshToken);
    }
}
