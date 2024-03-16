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
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<byte[]> postInference(@RequestBody InferenceRequest inferenceRequest, @PathVariable Long id) {
        try {
            byte[] image = modelService.sendInferenceWithModel(id, inferenceRequest.getInputs());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Image generation failed, Try again");
        }
    }

    @GetMapping
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        try {
            return new ResponseEntity<>(modelService.getAllModels(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not get data of all models");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getModelThumbnail(@PathVariable Long id) {
        try {
            byte[] imageData = modelService.getModelThumbnailById(id);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thumbnail could not be found");
        }
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModelResponse> createModel(@RequestPart MultipartFile file, @RequestPart ModelRequest modelRequest) {
        try {
            ModelResponse response = modelService.createModel(modelRequest, file);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Model creation failed, try again");
        }
    }
}
