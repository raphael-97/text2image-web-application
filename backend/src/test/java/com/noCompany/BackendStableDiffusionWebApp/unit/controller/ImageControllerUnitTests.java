package com.noCompany.BackendStableDiffusionWebApp.unit.controller;

import com.noCompany.BackendStableDiffusionWebApp.controller.ImageController;
import com.noCompany.BackendStableDiffusionWebApp.dto.ImageResponse;
import com.noCompany.BackendStableDiffusionWebApp.service.ImageStorageService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ImageControllerUnitTests {

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private ImageController imageController;

    @Test
    public void uploadImage_ReturnsSuccessResponse() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("fileName", "bytes".getBytes());
        Mockito.when(imageStorageService.storeUserImage(Mockito.any())).thenReturn(null);

        String expectedResponse = "Image uploaded successfully";

        ResponseEntity<String> expectedResult = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);
        ResponseEntity<String> stringResponseEntity = imageController.uploadImage(multipartFile);

        Assertions.assertThat(stringResponseEntity).isNotNull();
        Assertions.assertThat(stringResponseEntity).isEqualTo(expectedResult);
        Assertions.assertThat(stringResponseEntity.getStatusCode()).isEqualTo(expectedResult.getStatusCode());
        Assertions.assertThat(stringResponseEntity.getBody()).isEqualTo(expectedResult.getBody());
    }

    @Test
    public void uploadImage_throwsResponseStatusException() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("fileName", "bytes".getBytes());

        String message = "Some message";

        Mockito.when(imageStorageService.storeUserImage(Mockito.any())).thenThrow(new IOException(message));
        Assertions.assertThatThrownBy(() -> imageController.uploadImage(multipartFile))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }

    @Test
    public void getImage_ReturnsByteArrayResponse() throws IOException {
        Long id = 1L;

        byte[] byteResult = "test".getBytes();
        ResponseEntity<byte[]> expectedResult = ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(byteResult);

        Mockito.when(imageStorageService.getImageData(Mockito.any())).thenReturn(byteResult);
        ResponseEntity<byte[]> imageResponse = imageController.getImage(1L);

        Assertions.assertThat(imageResponse).isNotNull();
        Assertions.assertThat(imageResponse).isEqualTo(expectedResult);
        Assertions.assertThat(imageResponse.getStatusCode()).isEqualTo(expectedResult.getStatusCode());
        Assertions.assertThat(imageResponse.getBody()).isEqualTo(expectedResult.getBody());
    }

    @Test
    public void getImage_throwsResponseStatusException() throws IOException {
        Long id = 1L;
        String message = "Some message";

        Mockito.when(imageStorageService.getImageData(Mockito.any())).thenThrow(new IOException(message));
        Assertions.assertThatThrownBy(() -> imageController.getImage(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }

    @Test
    public void getImages_ReturnsListOfImagesResponse() {
        List<ImageResponse> results = List.of(new ImageResponse(), new ImageResponse());

        ResponseEntity<List<ImageResponse>> expectedResult = new ResponseEntity<>(results, HttpStatus.OK);

        Mockito.when(imageStorageService.findAllImagesByUser()).thenReturn(results);
        ResponseEntity<List<ImageResponse>> imageResponse = imageController.getImages();

        Assertions.assertThat(imageResponse).isNotNull();
        Assertions.assertThat(imageResponse).isEqualTo(expectedResult);
        Assertions.assertThat(imageResponse.getStatusCode()).isEqualTo(expectedResult.getStatusCode());
        Assertions.assertThat(imageResponse.getBody()).isEqualTo(expectedResult.getBody());
        Assertions.assertThat(imageResponse.getBody()).hasSize(2);
    }

    @Test
    public void getImages_throwsResponseStatusException() {
        Long id = 1L;
        String message = "Something went wrong.";

        Mockito.when(imageStorageService.findAllImagesByUser()).thenThrow(new NullPointerException());
        Assertions.assertThatThrownBy(() -> imageController.getImages())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }
}
