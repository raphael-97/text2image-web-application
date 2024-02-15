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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@ControllerAdvice
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

   @Autowired
    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> registerUser(@RequestBody RegisterRequest registerDto) {
        try {
            TokenResponse response = authService.registerUser(registerDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest loginDto) {
        try {
            TokenResponse response = authService.loginUser(loginDto);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong credentials");
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenResponse> getNewAccessAndRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
       try {
           TokenResponse newAccessAndRefreshToken = refreshTokenService.getNewAccessAndRefreshToken(refreshTokenRequest);
           return new ResponseEntity<>(newAccessAndRefreshToken, HttpStatus.OK);
       } catch (RuntimeException e) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
       }
    }
}
