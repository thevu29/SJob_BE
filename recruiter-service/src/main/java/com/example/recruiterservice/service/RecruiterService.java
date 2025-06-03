package com.example.recruiterservice.service;

import com.example.recruiterservice.client.FieldServiceClient;
import com.example.recruiterservice.client.UserServiceClient;
import com.example.recruiterservice.dto.Recruiter.GetRecruiterStatisticsDTO;
import com.example.recruiterservice.dto.Recruiter.RecruiterImportDTO;
import com.example.recruiterservice.dto.Recruiter.request.CreateRecruiterRequest;
import com.example.recruiterservice.dto.Recruiter.request.UpdateRecruiterRequest;
import com.example.recruiterservice.entity.Recruiter;
import com.example.recruiterservice.exception.FileUploadException;
import com.example.recruiterservice.mapper.RecruiterMapper;
import com.example.recruiterservice.repository.RecruiterRepository;
import com.example.recruiterservice.utils.helpers.CSVHelper;
import com.example.recruiterservice.utils.helpers.FileHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Field.FieldDTO;
import org.example.common.dto.Recruiter.RecruiterCreationDTO;
import org.example.common.dto.Recruiter.RecruiterDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.User.UserCreationDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;
    private final UserServiceClient userServiceClient;
    private final FieldServiceClient fieldServiceClient;
    private final RecruiterMapper recruiterMapper;
    private final FileHelper fileHelper;
    private final CSVHelper csvHelper;

    public GetRecruiterStatisticsDTO getRecruiterCountInMonth() {
        LocalDate today = LocalDate.now();

        int year = today.getYear();
        int month = today.getMonthValue();

        Integer count = recruiterRepository.countRecruitersInMonth(year, month);

        if (count == null) {
            count = 0;
        }

        Integer lastMonthCount = recruiterRepository.countRecruitersInMonth(year, month - 1);

        if (lastMonthCount == null) {
            lastMonthCount = 0;
        }

        double percentageChange = 0.0;
        if (lastMonthCount > 0) {
            percentageChange = ((double) (count - lastMonthCount) / lastMonthCount) * 100.0;
        } else if (count > 0) {
            percentageChange = 100.0;
        }

        return GetRecruiterStatisticsDTO.builder()
                .month(month)
                .recruiters(count)
                .percentageChange(percentageChange)
                .build();
    }

    public List<GetRecruiterStatisticsDTO> getRecruiterStatistics() {
        int year = Year.now().getValue();

        Map<Integer, Integer> monthlyCountMap = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyCountMap.put(i, 0);
        }

        List<Map<String, Object>> monthlyData = recruiterRepository.countRecruitersByMonthInYear(year);

        for (Map<String, Object> doc : monthlyData) {
            Integer month = (Integer) doc.get("_id");
            Integer count = (Integer) doc.get("count");

            if (month != null && count != null) {
                monthlyCountMap.put(month, count);
            }
        }

        List<GetRecruiterStatisticsDTO> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            GetRecruiterStatisticsDTO monthData = GetRecruiterStatisticsDTO.builder()
                    .month(month)
                    .recruiters(monthlyCountMap.get(month))
                    .build();

            result.add(monthData);
        }

        return result;
    }

    public Page<RecruiterWithUserDTO> findPagedRecruiters(
            String query,
            Boolean active,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        String columnName = switch (sortBy) {
            case "name" -> "name";
            case "updatedAt" -> "updated_at";
            case "createdAt" -> "created_at";
            default -> "id";
        };

        Sort sort = Sort.by(direction, columnName);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        boolean isActiveFilter = active != null;
        String sanitizedQuery = query == null ? "" : query;

        List<String> matchingUserIds = fetchMatchingUserIds(sanitizedQuery, active, size, sortBy, direction);

        if (matchingUserIds.isEmpty() && isActiveFilter) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        boolean isQueryBlank = sanitizedQuery.isBlank();

        if (isQueryBlank && matchingUserIds.isEmpty()) {
            Page<Recruiter> allRecruiters = recruiterRepository.findAll(pageable);
            return buildRecruiterWithUserPage(allRecruiters, pageable);
        }

        Page<Recruiter> recruiters = recruiterRepository.findBySearchCriteria(
                sanitizedQuery,
                matchingUserIds.isEmpty() ? Collections.emptyList() : matchingUserIds,
                pageable
        );

        if (recruiters.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        return buildRecruiterWithUserPage(recruiters, pageable);
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

    public RecruiterWithUserDTO getRecruiterByEmail(String email) {
        ApiResponse<UserDTO> userResponse = userServiceClient.getUserByEmail(email);
        UserDTO user = userResponse.getData();

        if (user == null) {
            throw new RuntimeException("Không tìm thấy user");
        }

        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà tuyển dụng"));

        FieldDTO field = getFieldById(recruiter.getFieldId());

        return recruiterMapper.toDtoWithField(
                recruiterMapper.toDto(recruiter),
                user,
                field
        );
    }

    public RecruiterWithUserDTO getRecruiterByUserId(String userId) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà tuyển dụng"));

        UserDTO user = getUserById(recruiter.getUserId());
        FieldDTO field = getFieldById(recruiter.getFieldId());

        return recruiterMapper.toDtoWithField(
                recruiterMapper.toDto(recruiter),
                user,
                field
        );
    }

    public RecruiterWithUserDTO createRecruiter(RecruiterCreationDTO request) {
        UserDTO user = null;

        try {
            UserCreationDTO createUserRequest = createUserRequest(request.getEmail(), request.getPassword());

            ApiResponse<UserDTO> userResponse = userServiceClient.createUser(createUserRequest);
            user = userResponse.getData();

            FieldDTO field = getFieldById(request.getFieldId());

            Recruiter recruiter = recruiterMapper.toEntity(request);
            recruiter.setUserId(user.getId());
            recruiter.setFieldId(field.getId());

            Recruiter savedRecruiter = recruiterRepository.save(recruiter);

            return recruiterMapper.toDto(recruiterMapper.toDto(savedRecruiter), user);
        } catch (Exception e) {
            handleUserRollback(user);
            throw new RuntimeException("Tạo nhà tuyển dụng thất bại", e);
        }
    }

    public void createRecruiterWithCSV(CreateRecruiterRequest request) {
        UserDTO user = null;
        try {
            UserCreationDTO createUserRequest = createUserRequest(request.getEmail(), request.getPassword());

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

            recruiterRepository.save(recruiter);
        } catch (Exception e) {
            handleUserRollback(user);
            throw new RuntimeException("Tạo nhà tuyển dụng thất bại", e);
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
            ApiResponse<List<FieldDTO>> fieldsResponse = fieldServiceClient.getFieldsByNames(new ArrayList<>(uniqueFieldNames));
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
                createRecruiterWithCSV(request);
            }
        } catch (IOException e) {
            throw new FileUploadException("Nhập file thất bại: " + e.getMessage());
        }
    }

    public List<RecruiterDTO> getRecruitersByNames(List<String> names) {
        List<Recruiter> recruiters = recruiterRepository.findByNameIn(names);
        return recruiters.stream()
                .map(recruiterMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RecruiterDTO> getRecruitersIds(List<String> ids) {
        List<Recruiter> recruiters = recruiterRepository.findByIdIn(ids);
        return recruiters.stream()
                .map(recruiterMapper::toDto)
                .collect(Collectors.toList());
    }

    private CreateRecruiterRequest convertToCreateRequest(RecruiterImportDTO dto, Map<String, String> fieldMap) {
        return CreateRecruiterRequest.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .fieldId(fieldMap.get(dto.getFieldName()))
                .name(dto.getName())
                .about(dto.getAbout())
                .website(dto.getWebsite())
                .address(dto.getAddress())
                .members(dto.getMembers())
                .build();
    }

    private UserCreationDTO createUserRequest(String email, String password) {
        return UserCreationDTO.builder()
                .email(email)
                .password(password)
                .role("RECRUITER")
                .build();
    }

    private UserDTO getUserById(String userId) {
        ApiResponse<UserDTO> response = userServiceClient.getUserById(userId);
        return response.getData();
    }

    private FieldDTO getFieldById(String fieldId) {
        ApiResponse<FieldDTO> response = fieldServiceClient.getField(fieldId);
        return response.getData();
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
                    FieldDTO field = getFieldById(recruiter.getFieldId());

                    return recruiterMapper.toDtoWithField(
                            recruiterMapper.toDto(recruiter),
                            userMap.get(recruiter.getUserId()),
                            field
                    );
                })
                .collect(Collectors.toList());
    }

    private List<String> fetchMatchingUserIds(String query, Boolean active, int size, String sortBy, Sort.Direction direction) {
        ApiResponse<List<UserDTO>> usersResponse = userServiceClient.findPagedUsers(
                query,
                active,
                "RECRUITER",
                1,
                size,
                sortBy,
                direction
        );

        if (usersResponse != null && usersResponse.getData() != null && !usersResponse.getData().isEmpty()) {
            return usersResponse.getData().stream()
                    .map(UserDTO::getId)
                    .toList();
        }

        return Collections.emptyList();
    }

    private Page<RecruiterWithUserDTO> buildRecruiterWithUserPage(Page<Recruiter> recruiters, Pageable pageable) {
        List<String> userIds = recruiters.getContent().stream()
                .map(Recruiter::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, UserDTO> userMap = fetchAndMapUsers(userIds);

        List<RecruiterWithUserDTO> content = mapToRecruiterWithUserDTOs(recruiters.getContent(), userMap);

        return new PageImpl<>(content, pageable, recruiters.getTotalElements());
    }

    private void handleUserRollback(UserDTO user) {
        if (user != null && user.getId() != null) {
            try {
                userServiceClient.deleteUser(user.getId());
            } catch (Exception ex) {
                log.error("Failed to roll back user creation: {}", user.getId(), ex);
            }
        }
    }
}
