package com.noCompany.BackendStableDiffusionWebApp.controller;
import com.noCompany.BackendStableDiffusionWebApp.dto.UserResponse;
import com.noCompany.BackendStableDiffusionWebApp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserInfo(JwtAuthenticationToken token) {
        try {
            UserResponse userDto = userService.findbyUsername(token.getName());
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User can not be found");
        }
    }
}
