package com.noCompany.BackendStableDiffusionWebApp.controller;

import com.noCompany.BackendStableDiffusionWebApp.dto.InferenceRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelResponse;
import com.noCompany.BackendStableDiffusionWebApp.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/models")
@EnableMethodSecurity
public class ModelController {

    private final ModelService modelService;

    @Autowired
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<byte[]> postInference(@RequestBody InferenceRequest inferenceRequest, @PathVariable Long id) throws IOException {
        byte[] image = modelService.sendInferenceWithModel(id, inferenceRequest.getInputs());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        return new ResponseEntity<>(modelService.getAllModels(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getModelThumbnail(@PathVariable Long id) throws IOException {
        byte[] imageData = modelService.getModelThumbnailById(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModelResponse> createModel(@RequestPart MultipartFile file, @RequestPart ModelRequest modelRequest) throws IOException {
        ModelResponse response = modelService.createModel(modelRequest, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
