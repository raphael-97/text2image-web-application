package com.noCompany.BackendStableDiffusionWebApp.jwtutils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateJwtToken(Authentication authentication) {
        Instant now = Instant.now();
        // 10 min
        long expiresIn = 60 * 10;
        String roles = authentication.getAuthorities().stream().
                map(GrantedAuthority::getAuthority).
                collect(Collectors.joining(","));

        JwtClaimsSet claim = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(authentication.getName())
                .claim("scope", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claim)).getTokenValue();
    }
}
