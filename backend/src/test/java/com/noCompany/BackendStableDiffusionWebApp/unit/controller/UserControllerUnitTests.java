package com.noCompany.BackendStableDiffusionWebApp.unit.controller;

import com.noCompany.BackendStableDiffusionWebApp.controller.UserController;
import com.noCompany.BackendStableDiffusionWebApp.dto.UserResponse;
import com.noCompany.BackendStableDiffusionWebApp.service.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTests {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void getUserInfo_ReturnsUserResponseEntity() {
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), Map.of("test", "test"), Map.of("test", "test"));
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

        UserResponse result = UserResponse.builder()
                .username("testUser")
                .email("test@gmail.com")
                .credits(5L)
                .build();

        Mockito.when(userService.findbyUsername(Mockito.any())).thenReturn(result);

        ResponseEntity<UserResponse> expectedResponse = new ResponseEntity<>(result, HttpStatus.OK);
        ResponseEntity<UserResponse> userResponseEntity = userController.getUserInfo(jwtAuthenticationToken);

        Assertions.assertThat(userResponseEntity).isNotNull();
        Assertions.assertThat(userResponseEntity).isEqualTo(expectedResponse);
        Assertions.assertThat(userResponseEntity.getStatusCode()).isEqualTo(expectedResponse.getStatusCode());
        Assertions.assertThat(userResponseEntity.getBody()).isEqualTo(expectedResponse.getBody());
    }

    @Test
    public void getUserInfo_throwsResponseStatusException(){
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), Map.of("test", "test"), Map.of("test", "test"));
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

        String message = "User can not be found";

        Mockito.when(userService.findbyUsername(Mockito.any())).thenThrow(new UsernameNotFoundException(message));
        Assertions.assertThatThrownBy(() -> userController.getUserInfo(jwtAuthenticationToken))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }
}
