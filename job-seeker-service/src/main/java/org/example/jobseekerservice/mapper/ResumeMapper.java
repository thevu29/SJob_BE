package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Resume.ResumeDTO;
import org.example.jobseekerservice.dto.Resume.request.CreateResumeRequest;
import org.example.jobseekerservice.dto.Resume.request.UpdateResumeRequest;
import org.example.jobseekerservice.entity.Resume;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResumeMapper extends BaseMapper {
    ResumeDTO toDto(Resume resume);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Resume toEntity(CreateResumeRequest request);

    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(UpdateResumeRequest request, @MappingTarget Resume resume);
}
