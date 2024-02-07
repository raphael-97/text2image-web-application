package com.noCompany.BackendStableDiffusionWebApp.jwtutils;

import com.noCompany.BackendStableDiffusionWebApp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    private final UserServiceImpl userService;

    @Value("${app.jwt.token.expiry.secondstime}")
    private Long refreshTokenDurationSeconds;

    @Autowired
    public JwtService(JwtEncoder jwtEncoder, UserServiceImpl userService) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    public String generateJwtToken(Authentication authentication) {
        Instant now = Instant.now();

        String roles = authentication.getAuthorities().stream().
                map(GrantedAuthority::getAuthority).
                collect(Collectors.joining(","));

        JwtClaimsSet claim = JwtClaimsSet.builder()
                .issuer("http://localhost.com")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenDurationSeconds))
                .subject(authentication.getName())
                .claim("authorities", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claim)).getTokenValue();
    }

    public String generateJwtTokenByUsername(String username) {
        Instant now = Instant.now();

        UserDetails userDetails = userService.loadUserByUsername(username);

        String roles = userDetails.getAuthorities().stream().
                map(GrantedAuthority::getAuthority).
                collect(Collectors.joining(","));

        JwtClaimsSet claim = JwtClaimsSet.builder()
                .issuer("http://localhost.com")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenDurationSeconds))
                .subject(userDetails.getUsername())
                .claim("authorities", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claim)).getTokenValue();
    }
}
