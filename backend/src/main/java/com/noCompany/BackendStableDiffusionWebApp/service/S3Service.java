package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service
public class S3Service {

    private S3Client s3Client;
    @Value("${app.storage.cloud.bucketname}")
    private String bucketName;

    public S3Service(@Value("${app.storage.local}") boolean storeLocally,
                     @Value("${app.storage.cloud.access-key}") String accessKey,
                     @Value("${app.storage.cloud.secret-key}") String secretKey,
                     @Value("${app.storage.cloud.region}") String regionString,
                     @Value("${app.storage.cloud.endpoint}") String endpoint) {
        if(!storeLocally) {
            Region region = Region.of(regionString);
            AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

            s3Client = S3Client.builder()
                    .region(region)
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(credentialsProvider)
                    .build();
        }
    }

    public void storeImage(String key, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .key(key)
                .build();
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
        s3Client.putObject(putObjectRequest, requestBody);
    }

    public byte[] downloadImage(Image image) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(image.getSavePathUrl())
                .build();
        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
        byte[] imagedata = object.readAllBytes();
        object.close();
        return imagedata;
    }
}
