package com.noCompany.BackendStableDiffusionWebApp.unit.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.ImageResponse;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.repository.ImageRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.ImageStorageService;
import com.noCompany.BackendStableDiffusionWebApp.service.S3Service;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ImageStorageServiceUnitTests {

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private S3Service s3Service;
    @InjectMocks
    private ImageStorageService imageStorageService;


    @Test
    public void storeUserImage_ReturnsSavedImage() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("file.jpg", "bytes".getBytes());
        User user = User.builder()
                .email("test@gmail.com")
                .username("testuser")
                .password("password")
                .provider(Provider.self)
                .credits(5L)
                .build();
        Image image = Image.builder()
                .savePathUrl("someURL")
                .fileName("fileName")
                .isModelThumbnail(false)
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(user.getUsername());
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(imageRepository.save(Mockito.any())).thenReturn(image);

        Image savedImage = imageStorageService.storeUserImage(multipartFile);

        Assertions.assertThat(savedImage).isNotNull();
        Assertions.assertThat(savedImage).isInstanceOf(Image.class);
        Assertions.assertThat(savedImage).isEqualTo(image);
    }

    @Test
    public void storeThumbnail_ReturnsSavedImage() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("file.jpg", "bytes".getBytes());
        User user = User.builder()
                .email("test@gmail.com")
                .username("testuser")
                .password("password")
                .provider(Provider.self)
                .credits(5L)
                .build();
        Image image = Image.builder()
                .savePathUrl("someURL")
                .fileName("fileName")
                .isModelThumbnail(false)
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(user.getUsername());
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(imageRepository.save(Mockito.any())).thenReturn(image);

        Image savedImage = imageStorageService.storeThumbnail(multipartFile);

        Assertions.assertThat(savedImage).isNotNull();
        Assertions.assertThat(savedImage).isInstanceOf(Image.class);
        Assertions.assertThat(savedImage).isEqualTo(image);
    }

    @Test
    public void getImageData_ReturnsByteArray() throws IOException {
        Long id = 1L;
        Image image = Image.builder()
                .fileName("fileName")
                .savePathUrl("savePathUrl")
                .isModelThumbnail(false)
                .build();

        byte[] imageData = "bytes".getBytes();
        Mockito.when(imageRepository.findImageByUserWithId(Mockito.any())).thenReturn(Optional.of(image));
        Mockito.when(imageStorageService.loadImage(Mockito.any())).thenReturn(imageData);
        byte[] returnedImageData = imageStorageService.getImageData(id);

        Assertions.assertThat(returnedImageData).isEqualTo(imageData);
    }

    @Test
    public void getThumbnailData_ReturnsByteArray() throws IOException {
        Long id = 1L;
        Image image = Image.builder()
                .fileName("fileName")
                .savePathUrl("savePathUrl")
                .isModelThumbnail(true)
                .build();

        byte[] imageData = "bytes".getBytes();
        Mockito.when(imageRepository.findThumbnailWithId(Mockito.any())).thenReturn(Optional.of(image));
        Mockito.when(imageStorageService.loadImage(Mockito.any())).thenReturn(imageData);
        byte[] returnedImageData = imageStorageService.getThumbnailData(id);

        Assertions.assertThat(returnedImageData).isEqualTo(imageData);
    }

    @Test
    public void findAllImagesByUser_ReturnsListOfImageResponses() {
        Image image1 = Image.builder()
                .fileName("image1")
                .savePathUrl("savePathUrl1")
                .isModelThumbnail(false)
                .build();
        Image image2 = Image.builder()
                .fileName("image2")
                .savePathUrl("savePathUrl2")
                .isModelThumbnail(true)
                .build();
        List<Image> imagesList = List.of(image1, image2);

        Mockito.when(imageRepository.findImagesByUser()).thenReturn(imagesList);

        ImageResponse imageResponse1 = new ImageResponse(image1.getId());
        ImageResponse imageResponse2 = new ImageResponse(image2.getId());

        List<ImageResponse> expectedResult = List.of(imageResponse1, imageResponse2);
        List<ImageResponse> result = imageStorageService.findAllImagesByUser();

        Assertions.assertThat(result).isEqualTo(expectedResult);
        Assertions.assertThat(result.size()).isEqualTo(expectedResult.size());
    }

    @Test
    public void imageToImageResponse() {
        Image image = Image.builder()
                .id(1L)
                .fileName("fileName")
                .savePathUrl("savePathUrl")
                .isModelThumbnail(false)
                .build();

        ImageResponse expectedResult = new ImageResponse(image.getId());
        ImageResponse imageResponse = imageStorageService.imageToImageResponse(image);

        Assertions.assertThat(imageResponse).isEqualTo(expectedResult);
        Assertions.assertThat(imageResponse.getId()).isEqualTo(expectedResult.getId());
    }
}
