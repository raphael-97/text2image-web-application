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
        byte[] image = modelService.sendInferenceWithModel(id, inferenceRequest.getInputs());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        return new ResponseEntity<>(modelService.getAllModels(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModelResponse> createModel(@RequestBody ModelRequest modelRequest) {
        ModelResponse response = modelService.createModel(modelRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
