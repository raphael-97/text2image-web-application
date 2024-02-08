package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.ImageResponse;
import com.noCompany.BackendStableDiffusionWebApp.repository.ImageRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageStorageService {

    private final ImageRepository imageRepository;

    private final UserRepository userRepository;

    private final String LOCAL_STORAGE_PATH = "src/main/resources/images/";


    @Autowired
    public ImageStorageService(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public void storeImage(MultipartFile file) {

        String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String filePath = LOCAL_STORAGE_PATH + UUID.randomUUID() + "." + filenameExtension;

        try {
            Image image = Image.builder()
                    .fileName(UUID.randomUUID().toString())
                    .savePathUrl(filePath)
                    .build();

            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("User not found"));
            image.setUser(user);
            imageRepository.save(image);

            file.transferTo(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Image could not be uploaded");
        }
    }

    public byte[] loadImage(Long id) {
        Optional<Image> imageById = imageRepository.findImageByUserWithId(id);
        Image image = imageById.orElseThrow(() -> new RuntimeException("User has no access to that image"));
        try {
            return Files.readAllBytes(Path.of(image.getSavePathUrl()));
        } catch (IOException e) {
            throw new RuntimeException("Image could not be read");
        }
    }

    public List<ImageResponse> findAllImagesByUser() {
        List<Image> imagesByUser = imageRepository.findImagesByUser();
        return imagesByUser.stream().map(this::imageToImageResponse).collect(Collectors.toList());
    }

    private ImageResponse imageToImageResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .build();
    }
}
