package com.noCompany.BackendStableDiffusionWebApp.unit.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.Model;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelResponse;
import com.noCompany.BackendStableDiffusionWebApp.repository.ModelRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.ImageStorageService;
import com.noCompany.BackendStableDiffusionWebApp.service.ModelService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ModelServiceUnitTests {

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private ImageStorageService imageStorageService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ModelService modelService;

    @Test
    public void getAllModels_ReturnsListOfModelResponses() {
        Image image1 = Image.builder().id(1L).build();
        Image image2 = Image.builder().id(2L).build();
        Model model1 = Model.builder()
                .id(1L)
                .image(image1)
                .name("model1")
                .inferenceUrl("inferenceurl1")
                .build();
        Model model2 = Model.builder()
                .id(2L)
                .image(image2)
                .name("model2")
                .inferenceUrl("inferenceurl2")
                .build();

        Mockito.when(modelRepository.findAll()).thenReturn(List.of(model1, model2));
        ModelResponse response1 = new ModelResponse(model1.getId(), model1.getName(), model1.getImage().getId());
        ModelResponse response2 = new ModelResponse(model2.getId(), model2.getName(), model2.getImage().getId());

        List<ModelResponse> expectedResult = List.of(response1, response2);
        List<ModelResponse> result = modelService.getAllModels();

        Assertions.assertThat(result).isEqualTo(expectedResult);
        Assertions.assertThat(result.size()).isEqualTo(expectedResult.size());
    }

    @Test
    public void createModel_ReturnsModelResponse() throws IOException {
        ModelRequest modelRequest = ModelRequest.builder()
                .name("model")
                .inferenceUrl("url")
                .build();
        MultipartFile multipartFile = new MockMultipartFile("file.jpg", "bytes".getBytes());

        Image image = Image.builder()
                .id(1L)
                .fileName("filename")
                .savePathUrl("savepathUrl")
                .isModelThumbnail(true)
                .build();

        Model model = Model.builder()
                .id(1L)
                .name(modelRequest.getName())
                .inferenceUrl(modelRequest.getInferenceUrl())
                .image(image)
                .build();

        Mockito.when(imageStorageService.storeThumbnail(Mockito.any())).thenReturn(image);
        Mockito.when(modelRepository.save(Mockito.any())).thenReturn(model);

        ModelResponse expectedResult = ModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .thumbnailImageId(image.getId())
                .build();

        ModelResponse result = modelService.createModel(modelRequest, multipartFile);
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void getModelThumbnailById_ReturnsByteArray() throws IOException {
        Long id = 1L;
        Image image = Image.builder().id(1L).build();
        Model model = Model.builder()
                .image(image)
                .build();

        byte[] expectedResult = "bytes".getBytes();
        Mockito.when(modelRepository.findById(Mockito.any())).thenReturn(Optional.of(model));
        Mockito.when(imageStorageService.getThumbnailData(Mockito.any())).thenReturn(expectedResult);

        byte[] result = modelService.getModelThumbnailById(id);

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }
}
