package com.noCompany.BackendStableDiffusionWebApp.unit.controller;

import com.noCompany.BackendStableDiffusionWebApp.controller.AuthController;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RefreshTokenRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.service.AuthService;
import com.noCompany.BackendStableDiffusionWebApp.service.RefreshTokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTests {
    @Mock
    private AuthService authService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    @Test
    public void registerUser_ReturnsTokenResponse() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@gmail.com")
                .provider(Provider.self)
                .username("testuser")
                .password("password")
                .build();
        TokenResponse tokenResponse = TokenResponse.builder()
                .refreshToken("refreshToken")
                .accessToken("accessToken")
                .build();
        ResponseEntity<TokenResponse> expectedResponse = new ResponseEntity<>(tokenResponse, HttpStatus.CREATED);

        Mockito.when(authService.registerUser(Mockito.any())).thenReturn(tokenResponse);

        ResponseEntity<TokenResponse> tokenResponseResponseEntity = authController.registerUser(registerRequest);

        Assertions.assertThat(tokenResponseResponseEntity).isNotNull();
        Assertions.assertThat(tokenResponseResponseEntity).isEqualTo(expectedResponse);
        Assertions.assertThat(tokenResponseResponseEntity.getStatusCode()).isEqualTo(expectedResponse.getStatusCode());
        Assertions.assertThat(tokenResponseResponseEntity.getBody()).isEqualTo(expectedResponse.getBody());
    }

    @Test
    public void registerUser_throwsResponseStatusException() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@gmail.com")
                .provider(Provider.self)
                .username("testuser")
                .password("password")
                .build();

        String message = "Some message";

        Mockito.when(authService.registerUser(Mockito.any())).thenThrow(new RuntimeException(message));
        Assertions.assertThatThrownBy(() -> authController.registerUser(registerRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }

    @Test
    public void loginUser_ReturnsTokenResponse() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .build();
        TokenResponse tokenResponse = TokenResponse.builder()
                .refreshToken("refreshToken")
                .accessToken("accessToken")
                .build();

        ResponseEntity<TokenResponse> expectedResponse = new ResponseEntity<>(tokenResponse, HttpStatus.ACCEPTED);

        Mockito.when(authService.loginUser(Mockito.any())).thenReturn(tokenResponse);

        ResponseEntity<TokenResponse> tokenResponseResponseEntity = authController.loginUser(loginRequest);

        Assertions.assertThat(tokenResponseResponseEntity).isNotNull();
        Assertions.assertThat(tokenResponseResponseEntity).isEqualTo(expectedResponse);
        Assertions.assertThat(tokenResponseResponseEntity.getStatusCode()).isEqualTo(expectedResponse.getStatusCode());
        Assertions.assertThat(tokenResponseResponseEntity.getBody()).isEqualTo(expectedResponse.getBody());
    }

    @Test
    public void loginUser_throwsResponseStatusException() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .build();

        Mockito.when(authService.loginUser(Mockito.any())).thenThrow(new RuntimeException());
        Assertions.assertThatThrownBy(() -> authController.loginUser(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Wrong credentials");
    }


    @Test
    public void requestNewAccessAndRefreshToken_ReturnsTokenResponse() {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();
        TokenResponse tokenResponse = TokenResponse.builder()
                .refreshToken("refreshToken")
                .accessToken("accessToken")
                .build();

        ResponseEntity<TokenResponse> expectedResponse = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        Mockito.when(refreshTokenService.getNewAccessAndRefreshToken(Mockito.any())).thenReturn(tokenResponse);

        ResponseEntity<TokenResponse> tokenResponseResponseEntity = authController.getNewAccessAndRefreshToken(refreshTokenRequest);

        Assertions.assertThat(tokenResponseResponseEntity).isNotNull();
        Assertions.assertThat(tokenResponseResponseEntity).isEqualTo(expectedResponse);
        Assertions.assertThat(tokenResponseResponseEntity.getStatusCode()).isEqualTo(expectedResponse.getStatusCode());
        Assertions.assertThat(tokenResponseResponseEntity.getBody()).isEqualTo(expectedResponse.getBody());
    }

    @Test
    public void requestNewAccessAndRefreshToken_throwsResponseStatusException() {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();

        String errorMessage = "error";

        Mockito.when(refreshTokenService.getNewAccessAndRefreshToken(Mockito.any())).thenThrow(new RuntimeException(errorMessage));
        Assertions.assertThatThrownBy(() -> authController.getNewAccessAndRefreshToken(refreshTokenRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(errorMessage);
    }
}
