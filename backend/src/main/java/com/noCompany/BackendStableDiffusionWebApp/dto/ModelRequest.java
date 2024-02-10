package com.noCompany.BackendStableDiffusionWebApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelRequest {
    private String name;
    private String inferenceUrl;
}
