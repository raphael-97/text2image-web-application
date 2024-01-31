package com.noCompany.BackendStableDiffusionWebApp.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterDto {
    private String username;
    private String email;
    private String password;
}
