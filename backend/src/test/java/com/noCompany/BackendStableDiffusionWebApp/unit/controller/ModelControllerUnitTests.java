package com.noCompany.BackendStableDiffusionWebApp.unit.controller;

import com.noCompany.BackendStableDiffusionWebApp.controller.ModelController;
import com.noCompany.BackendStableDiffusionWebApp.dto.InferenceRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelResponse;
import com.noCompany.BackendStableDiffusionWebApp.service.ModelService;
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
public class ModelControllerUnitTests {

    @Mock
    private ModelService modelService;

    @InjectMocks
    private ModelController modelController;

    @Test
    public void postInference_ReturnsByteArrayResponse() {
        Long id = 1L;
        InferenceRequest inferenceRequest = new InferenceRequest("modelInput");

        byte[] byteResult = "test".getBytes();
        ResponseEntity<byte[]> expectedResult = ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(byteResult);

        Mockito.when(modelService.sendInferenceWithModel(Mockito.any(), Mockito.any())).thenReturn(byteResult);
        ResponseEntity<byte[]> imageResponse = modelController.postInference(inferenceRequest, id);

        Assertions.assertThat(imageResponse).isNotNull();
        Assertions.assertThat(imageResponse).isEqualTo(expectedResult);
        Assertions.assertThat(imageResponse.getStatusCode()).isEqualTo(expectedResult.getStatusCode());
        Assertions.assertThat(imageResponse.getBody()).isEqualTo(expectedResult.getBody());
    }

    @Test
    public void postInference_throwsResponseStatusException(){
        Long id = 1L;
        InferenceRequest inferenceRequest = new InferenceRequest("modelInput");
        String message = "Image generation failed, Try again";

        Mockito.when(modelService.sendInferenceWithModel(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException());
        Assertions.assertThatThrownBy(() -> modelController.postInference(inferenceRequest, id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }

    @Test
    public void getAllModels_ReturnsListOfModelsResponse() {
        ModelResponse first = ModelResponse.builder()
                .name("first")
                .build();
        ModelResponse second = ModelResponse.builder()
                .name("second")
                .build();

        List<ModelResponse> results = List.of(first, second);
        Mockito.when(modelService.getAllModels()).thenReturn(results);
        ResponseEntity<List<ModelResponse>> expectedResult = new ResponseEntity<>(results, HttpStatus.OK);

        ResponseEntity<List<ModelResponse>> allModels = modelController.getAllModels();

        Assertions.assertThat(allModels).isNotNull();
        Assertions.assertThat(allModels).isEqualTo(expectedResult);
        Assertions.assertThat(allModels.getStatusCode()).isEqualTo(expectedResult.getStatusCode());
        Assertions.assertThat(allModels.getBody()).isEqualTo(expectedResult.getBody());
        Assertions.assertThat(allModels.getBody()).hasSize(2);
    }

    @Test
    public void getAllModels_throwsResponseStatusException(){

        String message = "Could not get data of all models";

        Mockito.when(modelService.getAllModels()).thenThrow(new NullPointerException());
        Assertions.assertThatThrownBy(() -> modelController.getAllModels())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }

    @Test
    public void getModelThumbnail_ReturnsByteArrayResponse() throws IOException {
        Long id = 1L;

        byte[] byteResult = "test".getBytes();
        ResponseEntity<byte[]> expectedResult = ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(byteResult);

        Mockito.when(modelService.getModelThumbnailById(Mockito.any())).thenReturn(byteResult);
        ResponseEntity<byte[]> imageResponse = modelController.getModelThumbnail(id);

        Assertions.assertThat(imageResponse).isNotNull();
        Assertions.assertThat(imageResponse).isEqualTo(expectedResult);
        Assertions.assertThat(imageResponse.getStatusCode()).isEqualTo(expectedResult.getStatusCode());
        Assertions.assertThat(imageResponse.getBody()).isEqualTo(expectedResult.getBody());
    }

    @Test
    public void getModelThumbnail_throwsResponseStatusException() throws IOException {
        Long id = 1L;
        String message = "Thumbnail could not be found";

        Mockito.when(modelService.getModelThumbnailById(Mockito.any())).thenThrow(new IOException());
        Assertions.assertThatThrownBy(() -> modelController.getModelThumbnail(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }

    @Test
    public void createModel_ReturnsModelResponseEntity() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("fileName", "bytes".getBytes());
        ModelRequest modelRequest = ModelRequest.builder()
                .inferenceUrl("HuggingfaceAPIUrl")
                .name("modelName")
                .build();

        ModelResponse modelResponse = ModelResponse.builder()
                .name(modelRequest.getName())
                .thumbnailImageId(1L)
                .build();
        Mockito.when(modelService.createModel(Mockito.any(), Mockito.any())).thenReturn(modelResponse);

        ResponseEntity<ModelResponse> expectedResult = new ResponseEntity<>(modelResponse, HttpStatus.CREATED);
        ResponseEntity<ModelResponse> stringResponseEntity = modelController.createModel(multipartFile, modelRequest);

        Assertions.assertThat(stringResponseEntity).isNotNull();
        Assertions.assertThat(stringResponseEntity).isEqualTo(expectedResult);
        Assertions.assertThat(stringResponseEntity.getStatusCode()).isEqualTo(expectedResult.getStatusCode());
        Assertions.assertThat(stringResponseEntity.getBody()).isEqualTo(expectedResult.getBody());
    }

    @Test
    public void createModel_throwsResponseStatusException() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("fileName", "bytes".getBytes());
        ModelRequest modelRequest = ModelRequest.builder()
                .inferenceUrl("HuggingfaceAPIUrl")
                .name("modelName")
                .build();
        String message = "Model creation failed, try again";

        Mockito.when(modelService.createModel(Mockito.any(), Mockito.any())).thenThrow(new IOException());
        Assertions.assertThatThrownBy(() -> modelController.createModel(multipartFile, modelRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(message);
    }
}
