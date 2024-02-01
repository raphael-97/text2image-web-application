package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.UserDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterDto;
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

    public UserDto loginUser(LoginDto loginDto) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginDto.getEmail(), loginDto.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);

        User user = (User) authenticationResponse.getPrincipal();
        UserDto response = userService.getUserByEmail(user.getEmail());
        response.setJwtToken(jwtService.generateJwtToken(authenticationResponse));
        return response;
    }

    public UserDto registerUser(RegisterDto registerDto) {
        UserDto response = userService.registerUser(registerDto);
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(registerDto.getEmail(), registerDto.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        response.setJwtToken(jwtService.generateJwtToken(authenticationResponse));
        return response;
    }
}
