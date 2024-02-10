package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.Model;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelResponse;
import com.noCompany.BackendStableDiffusionWebApp.dto.huggingface.InferenceRequestToHuggingFace;
import com.noCompany.BackendStableDiffusionWebApp.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModelService {

    private final WebClient.Builder webClientBuilder;

    private final ModelRepository modelRepository;

    private final ImageStorageService imageStorageService;

    @Value("${app.huggingface.api.token}")
    private String HUGGINGFACE_API_TOKEN;


    @Autowired
    public ModelService(WebClient.Builder webClientBuilder,
                        ModelRepository modelRepository,
                        ImageStorageService imageStorageService) {
        this.webClientBuilder = webClientBuilder;
        this.modelRepository = modelRepository;
        this.imageStorageService = imageStorageService;
    }

    public List<ModelResponse> getAllModels() {
        List<Model> allSavedModels = modelRepository.findAll();
        return allSavedModels.stream().map(this::mapModelToModelResponse).collect(Collectors.toList());
    }

    public byte[] sendInferenceWithModel(Long modelId, String input) throws IOException {
        Optional<Model> modelByIdOpt = modelRepository.findById(modelId);
        Model model = modelByIdOpt.orElseThrow(() -> new RuntimeException("Model not found"));

        InferenceRequestToHuggingFace inferenceRequestToHuggingFace = new InferenceRequestToHuggingFace(input);

        return webClientBuilder.build()
                .post()
                .uri(model.getInferenceUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + HUGGINGFACE_API_TOKEN)
                .body(BodyInserters.fromValue(inferenceRequestToHuggingFace))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    public ModelResponse createModel(ModelRequest modelRequest, MultipartFile file) throws IOException {
        Image image = imageStorageService.storeThumbnail(file);
        Model model = Model.builder()
                .name(modelRequest.getName())
                .inferenceUrl(modelRequest.getInferenceUrl())
                .image(image)
                .build();

        Model savedModel = modelRepository.save(model);

        return ModelResponse.builder()
                .id(savedModel.getId())
                .name(savedModel.getName())
                .thumbnailImageId(savedModel.getImage().getId())
                .build();
    }

    public byte[] getModelThumbnailById(Long id) throws IOException {
        Model modelById = modelRepository.findById(id).orElseThrow(() -> new RuntimeException("Model does not exist"));
        return imageStorageService.getThumbnailData(modelById.getImage().getId());
    }


    private ModelResponse mapModelToModelResponse(Model model) {
        return ModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .thumbnailImageId(model.getImage().getId())
                .build();
    }
}
