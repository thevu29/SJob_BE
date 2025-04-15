package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Certifitcaion.CertificationCreationDTO;
import org.example.jobseekerservice.dto.Certifitcaion.CertificationDTO;
import org.example.jobseekerservice.dto.Certifitcaion.CertificationUpdateDTO;
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
    Certification toEntity(CertificationCreationDTO request);

    @Mapping(target = "issueDate", source = "issueDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "expireDate", source = "expireDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(CertificationUpdateDTO request, @MappingTarget Certification certification);
}
