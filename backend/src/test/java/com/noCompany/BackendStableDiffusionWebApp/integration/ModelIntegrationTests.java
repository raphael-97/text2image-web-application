package com.noCompany.BackendStableDiffusionWebApp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.Model;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.LoginRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.ImageRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.ModelRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.AuthService;
import com.noCompany.BackendStableDiffusionWebApp.service.ImageStorageService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({ "test" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ModelIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    private TokenResponse tokenResponse;

    private Image firstThumbnail;
    private Image secondThumbnail;


    @BeforeEach
    public void beforeEach() {
        User adminUser = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("password"))
                .provider(Provider.self)
                .credits(100L)
                .roles(Set.of(Role.USER, Role.ADMIN))
                .build();
        User user = userRepository.save(adminUser);

        Image thumbnail1 = Image.builder()
                .savePathUrl("src/main/resources/static/forest.jfif")
                .fileName("fileName")
                .isModelThumbnail(true)
                .user(user)
                .build();

        Image thumbnail2 = Image.builder()
                .savePathUrl("src/main/resources/static/sunset.jfif")
                .fileName("fileName")
                .isModelThumbnail(true)
                .user(user)
                .build();

        List<Image> images = imageRepository.saveAll(List.of(thumbnail1, thumbnail2));

        firstThumbnail = images.get(0);
        secondThumbnail = images.get(1);

        LoginRequest loginRequest = new LoginRequest(adminUser.getEmail(), "password");
        tokenResponse = authService.loginUser(loginRequest);
    }

    @AfterEach
    public void afterEach() {
        modelRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    static void removeUploadedImage() {
        Set<String> filesToKeep = Set.of("forest.jfif", "sunset.jfif");
        File folder = new File("src/main/resources/static");
        File[] files = folder.listFiles();
        for (File file : files) {
            if(!filesToKeep.contains(file.getName())) {
                file.delete();
            }
        }
    }

    @Test
    public void getAllModels_ReturnsListOfModelResponse() throws Exception {
        Model model1 = Model.builder()
                .name("modelName1")
                .image(firstThumbnail)
                .inferenceUrl("HuggingfaceInferenceUrl")
                .build();

        Model model2 = Model.builder()
                .name("modelName2")
                .image(secondThumbnail)
                .inferenceUrl("HuggingfaceInferenceUrl")
                .build();

        modelRepository.save(model1);
        modelRepository.save(model2);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/models"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("modelName1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("modelName2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].thumbnailImageId")
                        .value(firstThumbnail.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].thumbnailImageId")
                        .value(secondThumbnail.getId().toString()));
    }

    @Test
    public void getModelThumbnail_ReturnsByteArrayEntity() throws Exception {
        Model model1 = Model.builder()
                .name("modelName1")
                .image(firstThumbnail)
                .inferenceUrl("HuggingfaceInferenceUrl")
                .build();
        Model savedModel = modelRepository.save(model1);

        byte[] expectedBytes = imageStorageService.loadImage(firstThumbnail);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/models/" + savedModel.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(MockMvcResultMatchers.content().bytes(expectedBytes));
    }

    @Test
    public void createModel_ReturnsModelResponseEntityAndIsModelInTable() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        ModelRequest modelRequest = ModelRequest.builder()
                .inferenceUrl("HuggingfaceAPIUrl")
                .name("modelName")
                .build();

        MockMultipartFile file = new MockMultipartFile("file", "file.jpg",
                MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());

        MockMultipartFile modelrequest = new MockMultipartFile("modelRequest", "modelRequest",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(modelRequest).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/models")
                        .file(file)
                        .file(modelrequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("modelName"));
    }
}
