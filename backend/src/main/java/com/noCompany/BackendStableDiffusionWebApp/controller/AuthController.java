package com.noCompany.BackendStableDiffusionWebApp.controller;

import com.noCompany.BackendStableDiffusionWebApp.dto.UserDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterDto;
import com.noCompany.BackendStableDiffusionWebApp.service.AuthService;
import com.noCompany.BackendStableDiffusionWebApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

   @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping
    @RequestMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterDto registerDto) {
        UserDto response = userService.registerUser(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    @RequestMapping("/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody LoginDto loginDto) {
        UserDto response = authService.authenticateUser(loginDto);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping
    @RequestMapping("/test")
    public ResponseEntity<UserDto> sayHello() {
        UserDto userDto = UserDto.builder()
                .username("test")
                .email("test@gmail.com")
                .credits(10L)
                .build();
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
