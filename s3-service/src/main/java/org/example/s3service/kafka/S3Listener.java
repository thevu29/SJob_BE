package org.example.s3service.kafka;

import lombok.RequiredArgsConstructor;
import org.common.dto.S3.FileUploadedDTO;
import org.common.dto.S3.UploadFileDTO;
import org.example.s3service.service.S3Service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3Listener {
    private final S3Service s3Service;
    private final KafkaTemplate<String, FileUploadedDTO> kafkaTemplate;

    @KafkaListener(topics = "upload-file", groupId = "s3-service")
    public void listen(UploadFileDTO request) {
        String fileUrl = s3Service.uploadFile(request);

        FileUploadedDTO event = new FileUploadedDTO();
        event.setId(request.getId());
        event.setFileUrl(fileUrl);

        kafkaTemplate.send("file-uploaded", event);
    }

    @KafkaListener(topics = "delete-file", groupId = "s3-service")
    public void listen(String fileUrl) {
        s3Service.deleteFile(fileUrl);
    }
}
