package org.example.s3service.service;

import lombok.RequiredArgsConstructor;
import org.common.dto.S3.UploadFileDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.*;

@Service
@RequiredArgsConstructor
public class S3Service {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10mb
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "application/pdf"
    ));

    @Value("${aws.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;

    private void validateFile(byte[] fileContent, String contentType) {
        if (fileContent == null || fileContent.length == 0) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        if (fileContent.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 10MB");
        }

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only image or PDF files are allowed");
        }
    }

    public String uploadFile(UploadFileDTO file) {
        validateFile(file.getFileContent(), file.getContentType());

        String fileExtension = file.getFileName() != null
                ? file.getFileName().substring(file.getFileName().lastIndexOf("."))
                : "";

        String fileName = UUID.randomUUID() + fileExtension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getFileContent()));

        return "https://" + bucketName + ".s3." + s3Client.serviceClientConfiguration().region().id() + ".amazonaws.com/" + fileName;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String fileName = getFileNameFromUrl(fileUrl);

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + fileUrl, e);
        }
    }

    public String getFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }
}
