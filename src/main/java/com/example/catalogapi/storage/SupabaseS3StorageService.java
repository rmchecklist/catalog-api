package com.example.catalogapi.storage;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class SupabaseS3StorageService {

    private final S3Client s3Client;

    @Value("${supabase.s3.bucket-name}")
    private String bucketName;

    @Value("${supabase.project-reference}")
    private String projectReference;

    public SupabaseS3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {

        String key = "uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .acl("public-read")  // Optional
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(file.getBytes()));

        return key;
    }

    public String getPublicReadUrl(String key) {
        return ("""
                https://%s.supabase.co/storage/v1/object/public/%s/%s
                """).formatted(projectReference, bucketName, key).strip();
    }

    public URL generateSignedUrl(String key) {
        return s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucketName).key(key));
    }
}
