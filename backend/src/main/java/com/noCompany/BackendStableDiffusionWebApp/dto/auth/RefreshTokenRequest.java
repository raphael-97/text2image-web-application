package com.noCompany.BackendStableDiffusionWebApp.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    private String refreshToken;
}
