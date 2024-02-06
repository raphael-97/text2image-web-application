package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.RefreshToken;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RefreshTokenRequest;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.TokenResponse;
import com.noCompany.BackendStableDiffusionWebApp.jwtutils.JwtService;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    // 20 min
    private Long refreshTokenDurationSeconds = 60 * 20L;

    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;


    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(User user) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByUser(user);
        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshTokenInDB = refreshTokenOpt.get();
            refreshTokenInDB.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationSeconds));
            refreshTokenInDB.setRefreshToken(UUID.randomUUID().toString());
            return refreshTokenRepository.save(refreshTokenInDB);
        }
        RefreshToken refreshtoken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationSeconds))
                .build();
        return refreshTokenRepository.save(refreshtoken);
    }

    public TokenResponse getNewAccessAndRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByRefreshToken(refreshTokenRequest.getRefreshToken());
        if(refreshTokenOpt.isPresent()) {
            RefreshToken savedToken = refreshTokenOpt.get();
            if(savedToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                User user = savedToken.getUser();
                refreshTokenRepository.deleteById(savedToken.getId());
                return TokenResponse.builder()
                        .accessToken(jwtService.generateJwtTokenByUsername(user.getUsername()))
                        .refreshToken(createRefreshToken(user).getRefreshToken())
                        .build();
            }
        }
        throw new RuntimeException("Refresh token is not valid or expired!");
    }
}
