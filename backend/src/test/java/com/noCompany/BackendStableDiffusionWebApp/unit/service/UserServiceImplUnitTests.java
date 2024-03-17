package com.noCompany.BackendStableDiffusionWebApp.unit.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.UserResponse;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.UserServiceImpl;
import jakarta.persistence.PersistenceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .username("testuser")
                .email("testuser@gmail.com")
                .password("password")
                .roles(Set.of(Role.USER))
                .credits(5L)
                .build();
    }

    @Test
    public void loadUserByUsername_ReturnsUser() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(user));
        User userResult = (User) userService.loadUserByUsername("testuser");
        Assertions.assertThat(userResult).isEqualTo(user);
    }

    @Test
    public void registerUser_ReturnsUser() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .provider(Provider.self)
                .build();
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        User userResult = userService.registerUser(registerRequest);
        Assertions.assertThat(userResult).isEqualTo(user);
    }

    @Test
    public void registerUser_ThrowsException() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .provider(Provider.self)
                .build();
        String errorMessage = "Email already registered";
        Mockito.when(userRepository.save(Mockito.any())).thenThrow(new PersistenceException());
        Assertions.assertThatThrownBy(() -> userService.registerUser(registerRequest))
                .isInstanceOf(Exception.class)
                .hasMessageContaining(errorMessage);
    }

    @Test
    public void findByUsername_ReturnsUserResponse() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(user));
        UserResponse expectedResult = UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .credits(user.getCredits())
                .build();
        UserResponse result = userService.findbyUsername(user.getUsername());

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }
    @Test
    public void findByUserName_ThrowsUsernameNotFoundException() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> userService.findbyUsername(user.getUsername()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(String.format("User with username %s not in users database", user.getUsername()));
    }

    @Test
    public void getUserByEmail_ReturnsUser() {
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(user));
        User result = userService.getUserByEmail(user.getEmail());
        Assertions.assertThat(result).isEqualTo(user);
    }

    @Test
    public void getUserByEmail_ThrowsUsernameNotFoundException() {
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> userService.getUserByEmail(user.getEmail()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(String.format("User with email %s not in users database", user.getEmail()));
    }

    @Test
    public void existsByEmail_ReturnsBoolean() {
       Mockito.when(userRepository.existsByEmail(Mockito.any())).thenReturn(true);
       Boolean result = userService.existsByEmail(user.getEmail());
       Assertions.assertThat(result).isEqualTo(true);
    }
}
