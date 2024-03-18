package com.noCompany.BackendStableDiffusionWebApp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({ "test" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuthIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void afterEach() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void loginUser_ReturnsTokenResponseEntity() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("testuser@gmail.com")
                .username("testuser")
                .password("password")
                .provider(Provider.self)
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));
        Optional<User> userOpt = userRepository.findByUsername(registerRequest.getUsername());

        Assertions.assertThat(userOpt).isNotEmpty();

        LoginRequest loginRequest = LoginRequest.builder()
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    public void registerUser_ReturnsTokenResponseEntityAndUserIsInUserTable() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("testuser@gmail.com")
                .username("testuser")
                .password("password")
                .provider(Provider.self)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());

        Optional<User> userOpt = userRepository.findByUsername(registerRequest.getUsername());

        Assertions.assertThat(userOpt).isNotEmpty();

        User user = userOpt.get();

        Assertions.assertThat(user.getUsername()).isEqualTo(registerRequest.getUsername());
        Assertions.assertThat(user.getEmail()).isEqualTo(registerRequest.getEmail());
        Assertions.assertThat(user.getPassword()).isNotEqualTo(registerRequest.getPassword());
    }
}
