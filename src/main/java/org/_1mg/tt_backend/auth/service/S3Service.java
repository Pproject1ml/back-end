package org._1mg.tt_backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;
    private final String defaultImageUrl;

    @Autowired
    public S3Service(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucketName,
            @Value("${aws.default-image}") String defaultImageUrl) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.defaultImageUrl = defaultImageUrl;
    }

    // 📌 S3에 프로필 이미지 업로드 (경로 유지 & 덮어쓰기)
    public String uploadProfileImage(MultipartFile file, String userId) throws IOException {
        String key = "profile-images/" + userId + ".jpg"; // 사용자당 하나의 프로필 이미지 유지

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    // 📌 기본 이미지 반환
    public String getDefaultProfileImage() {
        return defaultImageUrl;
    }
}
