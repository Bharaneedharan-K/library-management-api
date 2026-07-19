package com.example.LibraryApplication.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "https://s3.amazonaws.com/" + bucketName + "/default-cover.png";
        }

        // 1. Configure S3 Client dynamically using credentials
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();

        // 2. Generate a unique filename to prevent overwrite conflicts
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            // 3. Prepare the upload payload
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFilename)
                    .contentType(file.getContentType())
                    .build();

            // 4. Send file to AWS S3
            s3Client.putObject(putOb, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()
            ));

            // 5. Construct and return the public URL of the image
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, uniqueFilename);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        } finally {
            s3Client.close();
        }
    }
}
