package com.noCompany.BackendStableDiffusionWebApp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.AuthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({ "test" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthService authService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TokenResponse tokenResponse;

    @BeforeEach
    void beforeAll() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("testuser@gmail.com")
                .username("testuser")
                .password("password")
                .build();
        tokenResponse = authService.registerUser(registerRequest);
    }

    @AfterEach
    void afterAll() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getUserInfo_ReturnsUserResponseEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.credits").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("testuser@gmail.com"));
    }

    @Test
    public void getUserInfo_ReturnsStatusUnauthorizedIfNoJwtProvided() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
