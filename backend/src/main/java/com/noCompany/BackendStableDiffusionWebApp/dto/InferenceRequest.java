package com.noCompany.BackendStableDiffusionWebApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InferenceRequest {
    private String inputs;
}
