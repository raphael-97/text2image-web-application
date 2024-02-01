package com.noCompany.BackendStableDiffusionWebApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {
    private String username;
    private String email;
    private Long credits;
    private String jwtToken;
}
