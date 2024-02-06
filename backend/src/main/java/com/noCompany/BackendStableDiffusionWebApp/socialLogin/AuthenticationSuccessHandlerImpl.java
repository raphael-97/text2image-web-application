package com.noCompany.BackendStableDiffusionWebApp.socialLogin;

import com.noCompany.BackendStableDiffusionWebApp.domain.RefreshToken;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.jwtutils.JwtService;
import com.noCompany.BackendStableDiffusionWebApp.service.RefreshTokenService;
import com.noCompany.BackendStableDiffusionWebApp.service.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    private final UserServiceImpl userService;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    @Autowired
    public AuthenticationSuccessHandlerImpl(UserServiceImpl userService, RefreshTokenService refreshTokenService,
                                            JwtService jwtService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;

        String providerString = authenticationToken.getAuthorizedClientRegistrationId();

        if (Provider.valueOf(providerString) == Provider.google) {
            Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();

            String OAuth2UserEmail = (String)attributes.get("email");

            if(!userService.existsByEmail(OAuth2UserEmail)) {
                RegisterRequest registerRequest = RegisterRequest.builder()
                        .username((String)attributes.get("name") + UUID.randomUUID())
                        .email(OAuth2UserEmail)
                        .password(null)
                        .provider(Provider.google)
                        .build();

                User user = userService.registerUser(registerRequest);
                setCookiesToUser(user, response);
                response.sendRedirect("http://localhost:3000/gallery");
            } else {
                User user = userService.getUserByEmail(OAuth2UserEmail);
                setCookiesToUser(user, response);
                response.sendRedirect("http://localhost:3000/gallery");
            }
        }
    }

    private void setCookiesToUser(User user, HttpServletResponse response) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        Cookie accessTokenCookie = new Cookie("accessToken",
                jwtService.generateJwtTokenByUsername(user.getUsername()));
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken.getRefreshToken());
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
    }
}
