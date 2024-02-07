package com.noCompany.BackendStableDiffusionWebApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelResponse {
    private Long id;
    private String name;
    private String imageUrl;
}
