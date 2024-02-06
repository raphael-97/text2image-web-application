package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.jwtutils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserServiceImpl userService,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public TokenResponse loginUser(LoginRequest loginDto) {
        User userByEmail = userService.getUserByEmail(loginDto.getEmail());
        if (userByEmail.getProvider() != Provider.self && userByEmail.getPassword() == null) {
            throw new RuntimeException("User registered with an other Provider");
        }
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(userByEmail.getUsername(), loginDto.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);

        return TokenResponse.builder()
                .accessToken(jwtService.generateJwtToken(authenticationResponse))
                .refreshToken(refreshTokenService.createRefreshToken(userByEmail).getRefreshToken())
                .build();
    }

    public TokenResponse registerUser(RegisterRequest registerDto) {
        User user = userService.registerUser(registerDto);
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(registerDto.getUsername(), registerDto.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        return TokenResponse.builder()
                .accessToken(jwtService.generateJwtToken(authenticationResponse))
                .refreshToken(refreshTokenService.createRefreshToken(user).getRefreshToken())
                .build();
    }
}
