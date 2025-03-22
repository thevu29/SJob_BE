package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Certifitcaion.CertificationDTO;
import org.example.jobseekerservice.dto.Certifitcaion.request.CreateCertificationRequest;
import org.example.jobseekerservice.dto.Certifitcaion.request.UpdateCertificationRequest;
import org.example.jobseekerservice.entity.Certification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CertificationMapper extends BaseMapper {
    @Mapping(target = "jobSeekerId", source = "jobSeeker", qualifiedByName = "jobSeekerToJobSeekerId")
    CertificationDTO toDto(Certification certification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issueDate", source = "issueDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "expireDate", source = "expireDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Certification toEntity(CreateCertificationRequest request);

    @Mapping(target = "issueDate", source = "issueDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "expireDate", source = "expireDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(UpdateCertificationRequest request, @MappingTarget Certification certification);
}
