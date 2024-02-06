package com.noCompany.BackendStableDiffusionWebApp.controller;
import com.noCompany.BackendStableDiffusionWebApp.dto.UserResponse;
import com.noCompany.BackendStableDiffusionWebApp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    @RequestMapping("")
    public ResponseEntity<UserResponse> getUserInfo(JwtAuthenticationToken token) {
        UserResponse userDto = userService.findbyUsername(token.getName());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
