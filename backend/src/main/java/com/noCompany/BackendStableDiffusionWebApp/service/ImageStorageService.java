package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.ImageResponse;
import com.noCompany.BackendStableDiffusionWebApp.repository.ImageRepository;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.net.URI;
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

    private final boolean storeLocally;

    private S3Client s3Client;

    private String bucketName;



    @Autowired
    public ImageStorageService(ImageRepository imageRepository,
                               UserRepository userRepository,
                               @Value("${app.storage.cloud.bucketname}") String bucketname,
                               @Value("${app.storage.cloud.access-key}") String accessKey,
                               @Value("${app.storage.cloud.secret-key}") String secretKey,
                               @Value("${app.storage.cloud.region}") String regionString,
                               @Value("${app.storage.cloud.endpoint}") String endpoint,
                               @Value("${app.storage.local}") boolean storeLocally
                               ) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.storeLocally = storeLocally;
        if (!storeLocally) {
            Region region = Region.of(regionString);
            AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

            s3Client = S3Client.builder()
                    .region(region)
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(credentialsProvider)
                    .build();
            this.bucketName = bucketname;
        }
    }

    public void storeImage(MultipartFile file) {

        String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String filePath = storeLocally ?
                LOCAL_STORAGE_PATH + UUID.randomUUID() + "." + filenameExtension :
                UUID.randomUUID() + "." + filenameExtension;

        Image image = Image.builder()
                .fileName(UUID.randomUUID().toString())
                .savePathUrl(filePath)
                .build();

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("User not found"));
        image.setUser(user);

        if(storeLocally) {
            try {
                file.transferTo(Path.of(filePath));
            } catch (IOException e) {
                throw new RuntimeException("Image could not be uploaded");
            }
        } else {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentType(MediaType.IMAGE_JPEG_VALUE)
                    .key(filePath)
                    .build();
            try {
                RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
                s3Client.putObject(putObjectRequest, requestBody);
            } catch(IOException e) {
                throw new RuntimeException("Image could not be uploaded to cloud");
            }
        }
        imageRepository.save(image);
    }

    public byte[] loadImage(Long id) {
        Optional<Image> imageById = imageRepository.findImageByUserWithId(id);
        Image image = imageById.orElseThrow(() -> new RuntimeException("User has no access to that image"));
        if(storeLocally) {

            try {
                return Files.readAllBytes(Path.of(image.getSavePathUrl()));
            } catch (IOException e) {
                throw new RuntimeException("Image could not be read");
            }
        } else {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(image.getSavePathUrl())
                    .build();
            ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
            try {
                byte[] imagedata =  object.readAllBytes();
                object.close();
                return imagedata;
            }catch (IOException e) {
                throw new RuntimeException("Image could not be read from cloud");
            }
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
