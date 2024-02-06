package com.noCompany.BackendStableDiffusionWebApp.dto.auth;

import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    private String refreshToken;
}
