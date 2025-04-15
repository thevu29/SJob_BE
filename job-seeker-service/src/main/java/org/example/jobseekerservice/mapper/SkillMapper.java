package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Skill.SkillCreationDTO;
import org.example.jobseekerservice.dto.Skill.SkillDTO;
import org.example.jobseekerservice.dto.Skill.SkillUpdateDTO;
import org.example.jobseekerservice.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SkillMapper extends BaseMapper {
    @Mapping(target = "jobSeekerId", source = "jobSeeker", qualifiedByName = "jobSeekerToJobSeekerId")
    SkillDTO toDto(Skill skill);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Skill toEntity(SkillCreationDTO request);

    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(SkillUpdateDTO request, @MappingTarget Skill skill);
}
