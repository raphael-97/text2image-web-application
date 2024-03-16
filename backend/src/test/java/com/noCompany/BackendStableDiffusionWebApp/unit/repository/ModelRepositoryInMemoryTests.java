package com.noCompany.BackendStableDiffusionWebApp.unit.repository;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.Model;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.ImageRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.ModelRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ModelRepositoryInMemoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ModelRepository modelRepository;

    private User user;
    private Image image;
    private Model model;

    @BeforeEach
    public void initUser() {
        user = User.builder()
                .email("test@gmail.com")
                .username("testuser")
                .password("password")
                .provider(Provider.self)
                .roles(Set.of(Role.ADMIN))
                .build();
        image = Image.builder()
                .user(user)
                // Model Thumbnail
                .isModelThumbnail(true)
                .fileName("image.jpg")
                .savePathUrl("somePath")
                .build();
        model = Model.builder()
                .name("ImageGenerationModel")
                .inferenceUrl("HuggingfaceApiUrl")
                .image(image)
                .build();

        userRepository.save(user);
        imageRepository.save(image);
    }
    @AfterEach
    public void deleteAll() {
        modelRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void SaveModel_ReturnsSavedModel() {
        Model savedModel = modelRepository.save(model);

        Assertions.assertThat(savedModel).isNotNull();
        Assertions.assertThat(savedModel.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedModel).isEqualTo(model);
    }

    @Test
    public void SaveModel_FindByModelNameReturnsSavedModel() {
        modelRepository.save(model);

        Optional<Model> savedModelOpt = modelRepository.findByName(model.getName());

        Assertions.assertThat(savedModelOpt).isNotEmpty();

        Model savedModel = savedModelOpt.get();

        Assertions.assertThat(savedModel).isNotNull();
        Assertions.assertThat(savedModel.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedModel).isEqualTo(model);
    }
}
