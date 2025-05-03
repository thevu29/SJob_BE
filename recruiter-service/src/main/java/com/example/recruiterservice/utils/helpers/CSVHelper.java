package com.example.recruiterservice.utils.helpers;

import com.example.recruiterservice.dto.Recruiter.RecruiterImportDTO;
import com.example.recruiterservice.exception.FileUploadException;
import com.example.recruiterservice.utils.validations.ValidationUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVHelper {
    private final ValidationUtils validationUtils;

    public static String TYPE = "text/csv";
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;

    public CSVHelper(ValidationUtils validationUtils) {
        this.validationUtils = validationUtils;
    }

    public void validateCSVFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("Vui lòng chọn file trước khi upload!");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("Kích thước file không được vượt quá 5MB!");
        }

        if (!TYPE.equals(file.getContentType())) {
            throw new FileUploadException("Chỉ cho phép upload file CSV!");
        }
    }

    public List<RecruiterImportDTO> csvToRecruiterImportDTOs(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<RecruiterImportDTO> recruiters = new ArrayList<>();
            int rowNumber = 1;

            for (CSVRecord csvRecord : csvParser) {
                rowNumber++; // Start from 2 as row 1 is header
                RecruiterImportDTO dto = RecruiterImportDTO.builder()
                        .email(csvRecord.get("Email"))
                        .password(csvRecord.get("Password"))
                        .fieldName(csvRecord.get("Field Name"))
                        .name(csvRecord.get("Recruiter Name"))
                        .about(csvRecord.get("About"))
                        .image(csvRecord.get("Image"))
                        .website(csvRecord.get("Website"))
                        .address(csvRecord.get("Address"))
                        .members(parseMembers(csvRecord.get("Members"), rowNumber))
                        .build();

                // Validate each record
                validationUtils.validateCSVRecord(dto, rowNumber);
                recruiters.add(dto);
            }

            return recruiters;
        } catch (IOException e) {
            throw new FileUploadException("Không thể phân tích tệp CSV: " + e.getMessage());
        }
    }

    private int parseMembers(String value, int rowNumber) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new FileUploadException(
                    String.format("Dòng %d: Số lượng nhân sự không hợp lệ", rowNumber)
            );
        }
    }
}
