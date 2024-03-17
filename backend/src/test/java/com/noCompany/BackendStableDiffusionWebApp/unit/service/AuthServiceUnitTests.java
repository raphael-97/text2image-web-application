package com.noCompany.BackendStableDiffusionWebApp.unit.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.RefreshToken;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.jwtutils.JwtService;
import com.noCompany.BackendStableDiffusionWebApp.service.AuthService;
import com.noCompany.BackendStableDiffusionWebApp.service.RefreshTokenService;
import com.noCompany.BackendStableDiffusionWebApp.service.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void loginUser_ReturnsTokenResponse() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("testuser@gmail.com")
                .password("password")
                .build();

        User user = User.builder()
                .email("testuser@gmail.com")
                .username("testuser")
                .password("password")
                .provider(Provider.self)
                .credits(5L)
                .build();

        String accessToken = "accessToken";
        RefreshToken refreshToken = RefreshToken.builder()
                .expiryDate(LocalDateTime.now().plusSeconds(3600))
                .refreshToken("refreshToken")
                .user(user)
                .build();

        Mockito.when(userService.getUserByEmail(Mockito.any())).thenReturn(user);
        Mockito.when(jwtService.generateJwtToken(Mockito.any())).thenReturn(accessToken);
        Mockito.when(refreshTokenService.createRefreshToken(Mockito.any())).thenReturn(refreshToken);

        TokenResponse tokenResponse = authService.loginUser(loginRequest);

        Assertions.assertThat(tokenResponse.getAccessToken()).isEqualTo(accessToken);
        Assertions.assertThat(tokenResponse.getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());
    }

    @Test
    public void loginUser_ThrowsRuntimeExceptionIfUserRegisteredWithAnOtherProvider() {
        User user = User.builder()
                .email("testuser@gmail.com")
                .username("testuser")
                .password(null)
                // Another provider
                .provider(Provider.google)
                .credits(5L)
                .build();
        Mockito.when(userService.getUserByEmail(Mockito.any())).thenReturn(user);
        LoginRequest loginRequest = LoginRequest.builder()
                .email("testuser@gmail.com")
                .password("password")
                .build();

        String errorMessage = "User registered with an other Provider";
        Assertions.assertThatThrownBy(() -> authService.loginUser(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(errorMessage);
    }

    @Test
    public void registerUser_ReturnsTokenResponse() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("testuser@gmail.com")
                .username("testuser")
                .password("password")
                .build();
        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                // Another provider
                .provider(Provider.self)
                .credits(5L)
                .build();
        String accessToken = "accessToken";
        RefreshToken refreshToken = RefreshToken.builder()
                .expiryDate(LocalDateTime.now().plusSeconds(3600))
                .refreshToken("refreshToken")
                .user(user)
                .build();

        Mockito.when(userService.registerUser(Mockito.any())).thenReturn(user);
        Mockito.when(jwtService.generateJwtToken(Mockito.any())).thenReturn(accessToken);
        Mockito.when(refreshTokenService.createRefreshToken(Mockito.any())).thenReturn(refreshToken);

        TokenResponse tokenResponse = authService.registerUser(registerRequest);

        Assertions.assertThat(tokenResponse.getAccessToken()).isEqualTo(accessToken);
        Assertions.assertThat(tokenResponse.getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());
    }
}
