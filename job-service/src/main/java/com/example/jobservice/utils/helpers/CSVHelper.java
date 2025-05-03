package com.example.jobservice.utils.helpers;


import com.example.jobservice.dto.Field.FieldImportDTO;
import com.example.jobservice.dto.FieldDetail.FieldDetailImportDTO;
import com.example.jobservice.dto.Job.JobImportDTO;
import com.example.jobservice.utils.validations.ValidationUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.common.exception.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


    public static List<FieldImportDTO> csvToFieldImportDTOs(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            return csvParser.getRecords().stream()
                    .map(csvRecord -> FieldImportDTO.builder()
                            .name(csvRecord.get("Name"))
                            .description(csvRecord.get("Description"))
                            .build())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Không thể phân tích tệp CSV. Vui lòng kiểm tra lại định dạng tệp!" + e.getMessage());
        }
    }

    public static List<FieldDetailImportDTO> csvToFieldDetailImportDTOs(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            return csvParser.getRecords().stream()
                    .map(csvRecord -> FieldDetailImportDTO.builder()
                            .field(csvRecord.get("Field"))
                            .fieldDetail(csvRecord.get("Field Detail"))
                            .build())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Không thể phân tích tệp CSV. Vui lòng kiểm tra lại định dạng tệp!" + e.getMessage());
        }
    }

    public List<JobImportDTO> csvToJobImportDTOs(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<JobImportDTO> jobs = new ArrayList<>();
            int rowNumber = 1;

            for (CSVRecord csvRecord : csvParser) {
                rowNumber++;
                JobImportDTO dto = JobImportDTO.builder()
                        .recruiter(csvRecord.get("Recruiter"))
                        .fieldDetails(csvRecord.get("Field Detail"))
                        .name(csvRecord.get("Job Name"))
                        .description(csvRecord.get("Description"))
                        .salary(Double.parseDouble(csvRecord.get("Salary")))
                        .requirement(csvRecord.get("Requirement"))
                        .benefit(csvRecord.get("Benefit"))
                        .deadline(LocalDate.parse(csvRecord.get("Deadline")))
                        .slots(Integer.parseInt(csvRecord.get("Slots")))
                        .type(csvRecord.get("Type"))
                        .education(csvRecord.get("Education"))
                        .experience(csvRecord.get("Experience"))
                        .build();

                validationUtils.validateCSVRecord(dto, rowNumber);
                jobs.add(dto);
            }

            return jobs;

        } catch (IOException e) {
            throw new FileUploadException("Không thể phân tích tệp CSV: " + e.getMessage());
        }
    }


}
