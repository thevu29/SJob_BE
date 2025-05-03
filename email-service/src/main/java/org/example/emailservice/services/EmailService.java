package org.example.emailservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Email.EmailMessageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${aws.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;
    private final JavaMailSender mailSender;

    public void sendEmail(EmailMessageDTO request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setText(request.getBody(), true);

        mailSender.send(message);
    }

    public void sendEmailWithAttachment(EmailMessageDTO request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setText(request.getBody(), true);

        if (request.getFileUrl() != null && !request.getFileUrl().isBlank()) {
            String fileName = extractFileNameFromUrl(request.getFileUrl());
            byte[] fileContent = downloadFileFromS3(fileName);

            helper.addAttachment(fileName, () -> new ByteArrayInputStream(fileContent));
        }

        mailSender.send(message);
    }

    private byte[] downloadFileFromS3(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3 with key: " + key, e);
        }
    }

    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("File URL must not be null or empty");
        }
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex == -1 || lastSlashIndex == fileUrl.length() - 1) {
            throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
        }
        return fileUrl.substring(lastSlashIndex + 1);
    }
}