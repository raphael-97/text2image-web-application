package com.noCompany.BackendStableDiffusionWebApp.unit.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.RefreshToken;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RefreshTokenRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.jwtutils.JwtService;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.RefreshTokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceUnitTests {
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    public void createRefreshToken_ReturnsRefreshToken() {
        User user = User.builder()
                .email("test@gmail.com")
                .username("testuser")
                .password("password")
                .credits(5L)
                .provider(Provider.self)
                .roles(Set.of(Role.USER))
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken("refreshToken")
                .expiryDate(LocalDateTime.now().plusSeconds(3600))
                .user(user)
                .build();

        Mockito.when(refreshTokenRepository.save(Mockito.any())).thenReturn(refreshToken);
        RefreshToken result = refreshTokenService.createRefreshToken(user);
        Assertions.assertThat(result).isEqualTo(refreshToken);
    }

    @Test
    public void getNewAccessAndRefreshToken_ReturnsTokenResponse() {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();
        User user = User.builder()
                .email("test@gmail.com")
                .username("testuser")
                .password("password")
                .credits(5L)
                .provider(Provider.self)
                .roles(Set.of(Role.USER))
                .build();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(3600))
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .build();

        String accessToken = "accessToken";
        Mockito.when(refreshTokenRepository.findByRefreshToken(Mockito.any())).thenReturn(Optional.of(refreshToken));
        Mockito.when(jwtService.generateJwtTokenByUsername(Mockito.any())).thenReturn(accessToken);
        Mockito.when(refreshTokenRepository.save(Mockito.any())).thenReturn(refreshToken);

        TokenResponse expectedResult = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();

        TokenResponse result = refreshTokenService.getNewAccessAndRefreshToken(refreshTokenRequest);
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void getNewAccessAndRefreshToken_ThrowsRuntimeException() {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();

        Mockito.when(refreshTokenRepository.findByRefreshToken(Mockito.any())).thenReturn(Optional.empty());

        String errorMessage = "Refresh token is not valid or expired!";

        Assertions.assertThatThrownBy(() -> refreshTokenService.getNewAccessAndRefreshToken(refreshTokenRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(errorMessage);
    }
}
