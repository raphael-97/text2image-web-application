package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.dto.UserDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenDto;
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

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserServiceImpl userService,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public TokenDto loginUser(LoginDto loginDto) {
        UserDto userByEmail = userService.getUserByEmail(loginDto.getEmail());
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(userByEmail.getUsername(), loginDto.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        return new TokenDto(jwtService.generateJwtToken(authenticationResponse));
    }

    public TokenDto registerUser(RegisterDto registerDto) {
        userService.registerUser(registerDto);
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(registerDto.getUsername(), registerDto.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        return new TokenDto(jwtService.generateJwtToken(authenticationResponse));
    }
}
