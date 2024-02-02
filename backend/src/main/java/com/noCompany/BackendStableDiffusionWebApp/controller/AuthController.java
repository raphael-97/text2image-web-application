package com.noCompany.BackendStableDiffusionWebApp.controller;

import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenDto;
import com.noCompany.BackendStableDiffusionWebApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

   @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    @RequestMapping("/register")
    public ResponseEntity<TokenDto> registerUser(@RequestBody RegisterDto registerDto) {
        TokenDto response = authService.registerUser(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    @RequestMapping("/login")
    public ResponseEntity<TokenDto> loginUser(@RequestBody LoginDto loginDto) {
        TokenDto response = authService.loginUser(loginDto);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
