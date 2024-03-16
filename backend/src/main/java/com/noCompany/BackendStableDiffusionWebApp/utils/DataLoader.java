package com.noCompany.BackendStableDiffusionWebApp.utils;

import com.noCompany.BackendStableDiffusionWebApp.domain.RefreshToken;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.ModelRequest;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.ModelRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.RefreshTokenRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import com.noCompany.BackendStableDiffusionWebApp.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthenticationManager authenticationManager;

    private final ModelService modelService;

    private final ModelRepository modelRepository;

    @Autowired
    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                                   RefreshTokenRepository refreshTokenRepository,
                                                   ModelRepository modelRepository,
                                                   AuthenticationManager authenticationManager,
                                                   ModelService modelService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.modelRepository = modelRepository;
        this.authenticationManager = authenticationManager;
        this.modelService = modelService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("password"))
                .provider(Provider.self)
                .credits(100L)
                .roles(Set.of(Role.USER, Role.ADMIN))
                .build();

        RefreshToken token = new RefreshToken();
        token.setRefreshToken("secure");
        token.setExpiryDate(LocalDateTime.now());
        token.setUser(user);

        if(!userRepository.existsByEmail(user.getEmail())) {
            userRepository.save(user);
            refreshTokenRepository.save(token);
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), "password");
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        DataLoader.CustomMultiPartFile sunsetFile = new DataLoader.CustomMultiPartFile(new File("src/main/resources/static/sunset.jfif"));
        ModelRequest stableDiffusionXL = ModelRequest.builder()
                .name("Stable-diffusion-xl-base-1.0")
                .inferenceUrl("https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-xl-base-1.0")
                .build();
        if(modelRepository.findByName(stableDiffusionXL.getName()).isEmpty())
            modelService.createModel(stableDiffusionXL, sunsetFile);

        DataLoader.CustomMultiPartFile forestFile = new DataLoader.CustomMultiPartFile(new File("src/main/resources/static/forest.jfif"));
        ModelRequest stableDiffusion2_1 = ModelRequest.builder()
                .name("Stable-diffusion-2-1")
                .inferenceUrl("https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-2-1")
                .build();
        if(modelRepository.findByName(stableDiffusion2_1.getName()).isEmpty())
            modelService.createModel(stableDiffusion2_1, forestFile);
    }

    private static class CustomMultiPartFile implements MultipartFile {

        private final File file;
        public CustomMultiPartFile(File file) {
            this.file = file;
        }

        @Override
        public String getName() {
            return file.getName();
        }

        @Override
        public String getOriginalFilename() {
            return file.getName();
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return file.length() == 0;
        }

        @Override
        public long getSize() {
            return file.length();
        }

        @Override
        public byte[] getBytes() throws IOException {
            return Files.readAllBytes(file.toPath());
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(file);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            FileCopyUtils.copy(getInputStream(), Files.newOutputStream(file.toPath()));
        }

        @Override
        public void transferTo(Path dest) throws IOException, IllegalStateException {
            FileCopyUtils.copy(getInputStream(), Files.newOutputStream(dest));
        }
    }
}
