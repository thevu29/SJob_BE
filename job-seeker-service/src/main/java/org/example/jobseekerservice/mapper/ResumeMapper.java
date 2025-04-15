package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Resume.ResumeCreationDTO;
import org.example.jobseekerservice.dto.Resume.ResumeDTO;
import org.example.jobseekerservice.dto.Resume.ResumeUpdateDTO;
import org.example.jobseekerservice.entity.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResumeMapper extends BaseMapper {
    ResumeDTO toDto(Resume resume);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Resume toEntity(ResumeCreationDTO request);

    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(ResumeUpdateDTO request, @MappingTarget Resume resume);
}
