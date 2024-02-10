package com.noCompany.BackendStableDiffusionWebApp.config;
import com.noCompany.BackendStableDiffusionWebApp.socialLogin.AuthenticationFailureHandlerImpl;
import com.noCompany.BackendStableDiffusionWebApp.socialLogin.AuthenticationSuccessHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
    private final AuthenticationFailureHandlerImpl authenticationFailureHandler;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Autowired
    public SecurityConfig(AuthenticationSuccessHandlerImpl authenticationSuccessHandler,
                          AuthenticationFailureHandlerImpl authenticationFailureHandler,
                          JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/models/**", "/api/auth/**", "/error").permitAll()
                        .requestMatchers("/api/image/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                //.httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(configure -> configure.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
