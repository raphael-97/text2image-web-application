package com.noCompany.BackendStableDiffusionWebApp.controller;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RefreshTokenRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.service.AuthService;
import com.noCompany.BackendStableDiffusionWebApp.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

   @Autowired
    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping
    @RequestMapping("/register")
    public ResponseEntity<TokenResponse> registerUser(@RequestBody RegisterRequest registerDto) {
        TokenResponse response = authService.registerUser(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    @RequestMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest loginDto) {
        TokenResponse response = authService.loginUser(loginDto);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenResponse> getNewAccessAndRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        TokenResponse newAccessAndRefreshToken = refreshTokenService.getNewAccessAndRefreshToken(refreshTokenRequest);
        return new ResponseEntity<>(newAccessAndRefreshToken, HttpStatus.OK);
    }
}
