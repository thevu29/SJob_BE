package com.example.recruiterservice.service;

import com.example.recruiterservice.client.JobServiceClient;
import com.example.recruiterservice.client.UserServiceClient;
import com.example.recruiterservice.dto.*;
import com.example.recruiterservice.dto.request.CreateRecruiterRequest;
import com.example.recruiterservice.dto.request.CreateUserRequest;
import com.example.recruiterservice.dto.request.UpdateRecruiterRequest;
import com.example.recruiterservice.dto.response.ApiResponse;
import com.example.recruiterservice.entity.Recruiter;
import com.example.recruiterservice.exception.FileUploadException;
import com.example.recruiterservice.mapper.RecruiterMapper;
import com.example.recruiterservice.repository.RecruiterRepository;
import com.example.recruiterservice.utils.helpers.CSVHelper;
import com.example.recruiterservice.utils.helpers.FileHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;
    private final UserServiceClient userServiceClient;
    private final JobServiceClient jobServiceClient;
    private final RecruiterMapper recruiterMapper;
    private final FileHelper fileHelper;
    private final CSVHelper csvHelper;

    private UserDTO getUserById(String userId) {
        ApiResponse<UserDTO> response = userServiceClient.getUserById(userId);
        return response.getData();
    }

    private FieldDTO getFieldById(String fieldId) {
        ApiResponse<FieldDTO> response = jobServiceClient.getField(fieldId);
        return response.getData();
    }

    public RecruiterWithUserDTO getRecruiterById(String id) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà tuyển dụng"));

        UserDTO user = getUserById(recruiter.getUserId());
        FieldDTO field = getFieldById(recruiter.getFieldId());

        return recruiterMapper.toDtoWithField(
                recruiterMapper.toDto(recruiter),
                user,
                field
        );
    }

    private Map<String, UserDTO> fetchAndMapUsers(List<String> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        ApiResponse<List<UserDTO>> response = userServiceClient.getUsersByIds(userIds);

        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .collect(Collectors.toMap(
                            UserDTO::getId,
                            Function.identity(),
                            (existing, replacement) -> existing
                    ));
        }

        return Collections.emptyMap();
    }

    private List<RecruiterWithUserDTO> mapToRecruiterWithUserDTOs(
            List<Recruiter> recruiters,
            Map<String, UserDTO> userMap
    ) {

        return recruiters.stream()
                .filter(r -> r.getUserId() != null && userMap.containsKey(r.getUserId()))
                .map(recruiter -> {
                    FieldDTO field = null;
                    field = getFieldById(recruiter.getFieldId());
                    return recruiterMapper.toDtoWithField(
                            recruiterMapper.toDto(recruiter),
                            userMap.get(recruiter.getUserId()),
                            field
                    );
                })
                .collect(Collectors.toList());
    }

    public List<RecruiterWithUserDTO> getAllRecruiters() {
        List<Recruiter> recruiters = recruiterRepository.findAll();

        // Get all user IDs
        List<String> userIds = recruiters.stream()
                .map(Recruiter::getUserId)
                .collect(Collectors.toList());

        Map<String, UserDTO> userMap = fetchAndMapUsers(userIds);

        return mapToRecruiterWithUserDTOs(recruiters, userMap);

    }

    public RecruiterWithUserDTO createRecruiter(CreateRecruiterRequest request) {
        UserDTO user = null;
        try{
            CreateUserRequest createUserRequest = CreateUserRequest.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role("RECRUITER")
                    .build();
            ApiResponse<UserDTO> userResponse = userServiceClient.createUser(createUserRequest);
            user = userResponse.getData();

            Recruiter recruiter = recruiterMapper.toEntity(request);
            recruiter.setUserId(user.getId());

            if (request.getImage() != null && !request.getImage().isEmpty()) {
                try {
                    String imageUrl = fileHelper.uploadFile(request.getImage());
                    recruiter.setImage(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Upload ảnh thất bại", e);
                }
            }

            Recruiter savedRecruiter = recruiterRepository.save(recruiter);
            return recruiterMapper.toDto(recruiterMapper.toDto(savedRecruiter), user);


        } catch (Exception e) {
            handleUserRollback(user);
            throw new RuntimeException("Tạo nhà tuyển dụng thất bại", e);
        }

    }

    public void handleUserRollback(UserDTO user) {
        if (user != null && user.getId() != null) {
            try {
                log.info("Rolling back user creation: {}", user.getId());
                userServiceClient.deleteUser(user.getId());
            } catch (Exception ex) {
                log.error("Failed to roll back user creation: {}", user.getId(), ex);
            }
        }
    }

    public boolean checkIfRecruiterExists(String id) {
        return recruiterRepository.existsById(id);
    }

    public RecruiterWithUserDTO updateRecruiter(String id, UpdateRecruiterRequest request) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà tuyển dụng"));

        UserDTO user = getUserById(recruiter.getUserId());

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                if (recruiter.getImage() != null && !recruiter.getImage().isEmpty()) {
                    fileHelper.deleteFile(recruiter.getImage());
                }

                String imageUrl = fileHelper.uploadFile(request.getImage());
                recruiter.setImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Upload ảnh thất bại", e);
            }
        }
        recruiterMapper.toEntity(request, recruiter);
        Recruiter updatedRecruiter = recruiterRepository.save(recruiter);

        return recruiterMapper.toDto(recruiterMapper.toDto(updatedRecruiter), user);
    }

    public void deleteRecruiter(String id) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà tuyển dụng"));

        UserDTO user = getUserById(recruiter.getUserId());

        recruiterRepository.delete(recruiter);
        userServiceClient.deleteUser(user.getId());
    }

    public void importFileCSV(MultipartFile file) {

        try {
            // Validate file
            csvHelper.validateCSVFile(file);

            // Parse CSV to DTO
            List<RecruiterImportDTO> recruiterImportDTOS = csvHelper.csvToRecruiterImportDTOs(file.getInputStream());

            // 1. Extract unique field names
            Set<String> uniqueFieldNames = recruiterImportDTOS.stream()
                    .map(RecruiterImportDTO::getFieldName)
                    .collect(Collectors.toSet());

            // 2. Fetch all fields in one query
            ApiResponse<List<FieldDTO>> fieldsResponse = jobServiceClient.getFieldsByNames(new ArrayList<>(uniqueFieldNames));
            Map<String, String> fieldMap = fieldsResponse.getData().stream()
                    .collect(Collectors.toMap(FieldDTO::getName, FieldDTO::getId));

            // 3. Validate all fields exist
            Set<String> missingFields = uniqueFieldNames.stream()
                    .filter(name -> !fieldMap.containsKey(name))
                    .collect(Collectors.toSet());
            if (!missingFields.isEmpty()) {
                throw new FileUploadException("Không tìm thấy các ngành nghề sau: " + String.join(", ", missingFields));
            }

            // 4. Create recruiters using existing method
            for (RecruiterImportDTO dto : recruiterImportDTOS) {
                CreateRecruiterRequest request = convertToCreateRequest(dto, fieldMap);
                createRecruiter(request);
            }
        } catch (IOException e) {
            throw new FileUploadException("Nhập file thất bại: " + e.getMessage());
        }
    }

    private CreateRecruiterRequest convertToCreateRequest(RecruiterImportDTO dto, Map<String, String> fieldMap) {
        return CreateRecruiterRequest.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .fieldId(fieldMap.get(dto.getFieldName()))
                .name(dto.getName())
                .about(dto.getAbout())
//                .image(dto.getImage())
                .website(dto.getWebsite())
                .address(dto.getAddress())
                .members(dto.getMembers())
                .build();
    }

    public List<RecruiterDTO> getRecruitersByNames(List<String> names) {
        List<Recruiter> recruiters = recruiterRepository.findByNameIn(names);
        return recruiters.stream()
                .map(recruiterMapper::toDto)
                .collect(Collectors.toList());
    }
}
