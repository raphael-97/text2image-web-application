package com.noCompany.BackendStableDiffusionWebApp.integration;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.repository.ImageRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.AuthService;
import com.noCompany.BackendStableDiffusionWebApp.service.ImageStorageService;
import org.assertj.core.api.Assertions;
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
public class ImageIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private AuthService authService;

    private TokenResponse tokenResponse;
    private MockMultipartFile multipartFile;
    private User user;

    @BeforeEach
    void beforeEach() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("testuser@gmail.com")
                .username("testuser")
                .password("password")
                .build();
        tokenResponse = authService.registerUser(registerRequest);
        user = userRepository.findByUsername(registerRequest.getUsername()).orElseThrow();
        multipartFile = new MockMultipartFile("file", "file.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());
    }
    @AfterEach
    void afterEach() {
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
    public void uploadImage_ReturnsSuccessResponseAndImageIsInTable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/image")
                .file(multipartFile)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Image uploaded successfully"));

        List<Image> imagesList = imageRepository.findAll();

        Assertions.assertThat(imagesList).hasSize(1);
        Image savedImage = imagesList.getFirst();
        Assertions.assertThat(savedImage.getUser().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void uploadImage_ReturnsStatusUnauthorizedIfNoJwtProvided() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/image")
                .file(multipartFile)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getImage_ReturnsByteArrayEntity() throws Exception {
        Image image = Image.builder()
                .fileName("forest.jfif")
                .isModelThumbnail(false)
                .savePathUrl("src/main/resources/static/forest.jfif")
                .user(user)
                .build();
        Image savedImage = imageRepository.save(image);

        byte[] expectedBytes = imageStorageService.loadImage(savedImage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/image/" + savedImage.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(MockMvcResultMatchers.content().bytes(expectedBytes));
    }

    @Test
    public void getImage_ReturnsStatusUnauthorizedIfNoJwtProvided() throws Exception {
        Image image = Image.builder()
                .fileName("forest.jfif")
                .isModelThumbnail(false)
                .savePathUrl("src/main/resources/static/forest.jfif")
                .user(user)
                .build();
        Image savedImage = imageRepository.save(image);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/image/" + savedImage.getId())
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getImages_ReturnsListOfImageResponses() throws Exception {
        Image image1 = Image.builder()
                .fileName("forest.jfif")
                .isModelThumbnail(false)
                .savePathUrl("src/main/resources/static/forest.jfif")
                .user(user)
                .build();
        Image image2 = Image.builder()
                .fileName("sunset.jfif")
                .isModelThumbnail(false)
                .savePathUrl("src/main/resources/static/sunset.jfif")
                .user(user)
                .build();
        imageRepository.save(image1);
        imageRepository.save(image2);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/image")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"));
    }

    @Test
    public void getImages_ReturnsStatusUnauthorizedIfNoJwtProvided() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/image")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
