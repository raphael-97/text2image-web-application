package com.noCompany.BackendStableDiffusionWebApp.unit.repository;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.ImageRepository;
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
public class ImageRepositoryInMemoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;

    private User user;
    private Image image;

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
                // Not a model thumbnail => generated image
                .isModelThumbnail(false)
                .fileName("image.jpg")
                .savePathUrl("somePath")
                .build();

        userRepository.save(user);
    }

    @AfterEach
    public void deleteAll() {
        imageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void UserSavesImage_ReturnsSavedImage() {
        Image savedImage = imageRepository.save(image);

        Assertions.assertThat(savedImage).isNotNull();
        Assertions.assertThat(savedImage.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedImage.getUser()).isEqualTo(user);
        Assertions.assertThat(savedImage).isEqualTo(image);
    }

    @Test
    public void UserSavesThumbnail_FindThumbnailWithId() {
        image.setModelThumbnail(true);
        Long thumbnailId = imageRepository.save(image).getId();

        Optional<Image> savedThumbnailOpt = imageRepository.findThumbnailWithId(thumbnailId);

        Assertions.assertThat(savedThumbnailOpt).isNotEmpty();

        Image savedThumbnail = savedThumbnailOpt.get();

        Assertions.assertThat(savedThumbnail).isNotNull();
        Assertions.assertThat(savedThumbnail.getId()).isGreaterThan(0L);
        Assertions.assertThat(savedThumbnail).isEqualTo(image);
    }
}
