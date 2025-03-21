package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Skill.SkillDTO;
import org.example.jobseekerservice.dto.Skill.request.CreateSkillRequest;
import org.example.jobseekerservice.dto.Skill.request.UpdateSkillRequest;
import org.example.jobseekerservice.entity.Skill;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SkillMapper extends BaseMapper {
    @Mapping(target = "jobSeekerId", source = "jobSeeker", qualifiedByName = "jobSeekerToJobSeekerId")
    SkillDTO toDto(Skill skill);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Skill toEntity(CreateSkillRequest request);

    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(UpdateSkillRequest request, @MappingTarget Skill skill);
}
