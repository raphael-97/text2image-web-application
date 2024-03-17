package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.ImageResponse;
import com.noCompany.BackendStableDiffusionWebApp.repository.ImageRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Getter
public class ImageStorageService {

    private final ImageRepository imageRepository;

    private final UserRepository userRepository;

    @Value("${app.storage.local.path}")
    private String LOCAL_STORAGE_PATH;

    @Value("${app.storage.local}")
    private boolean storeLocally;

    private final S3Service s3Service;


    @Autowired
    public ImageStorageService(ImageRepository imageRepository,
                               UserRepository userRepository,
                               S3Service s3Service) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    public Image storeUserImage(MultipartFile file) throws IOException {
        String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String filePath = storeLocally ?
                LOCAL_STORAGE_PATH + UUID.randomUUID() + "." + filenameExtension :
                UUID.randomUUID() + "." + filenameExtension;

        Image image = Image.builder()
                .fileName(UUID.randomUUID().toString())
                .savePathUrl(filePath)
                .isModelThumbnail(false)
                .build();

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("User not found"));
        image.setUser(user);

        if (storeLocally)
            file.transferTo(Path.of(filePath));
        else
            s3Service.storeImage(filePath, file);

        return imageRepository.save(image);
    }

    public Image storeThumbnail(MultipartFile file) throws IOException {
        String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String filePath = storeLocally ?
                LOCAL_STORAGE_PATH + UUID.randomUUID() + "." + filenameExtension :
                UUID.randomUUID() + "." + filenameExtension;

        Image image = Image.builder()
                .fileName(UUID.randomUUID().toString())
                .savePathUrl(filePath)
                .isModelThumbnail(true)
                .build();

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("User not found"));
        image.setUser(user);

        if (storeLocally)
            file.transferTo(Path.of(filePath));
        else
            s3Service.storeImage(filePath, file);

        return imageRepository.save(image);
    }

    public byte[] getImageData(Long id) throws IOException {
        Optional<Image> imageById = imageRepository.findImageByUserWithId(id);
        Image image = imageById.orElseThrow(() -> new RuntimeException("User has no access to that image"));
        return loadImage(image);
    }

    public byte[] getThumbnailData(Long id) throws IOException {
        Optional<Image> imageById = imageRepository.findThumbnailWithId(id);
        Image image = imageById.orElseThrow(() -> new RuntimeException("User has no access to that image"));
        return loadImage(image);
    }


    public List<ImageResponse> findAllImagesByUser() {
        List<Image> imagesByUser = imageRepository.findImagesByUser();
        return imagesByUser.stream().map(this::imageToImageResponse).collect(Collectors.toList());
    }

    public byte[] loadImage(Image image) throws IOException {
        if(storeLocally) {
            return Files.readAllBytes(Path.of(image.getSavePathUrl()));
        } else {
            return s3Service.downloadImage(image);
        }
    }

    public ImageResponse imageToImageResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .build();
    }
}
