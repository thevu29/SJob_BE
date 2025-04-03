package com.example.jobservice.utils.helpers;


import com.example.jobservice.dto.Field.FieldImportDTO;
import com.example.jobservice.dto.FieldDetail.FieldDetailImportDTO;
import com.example.jobservice.exception.FileUploadException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class CSVHelper {
    public static String TYPE = "text/csv";
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;

    public static void validateCSVFile(MultipartFile file) {
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
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

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
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

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

}
