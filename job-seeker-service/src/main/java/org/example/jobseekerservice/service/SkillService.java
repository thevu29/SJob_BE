package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.exception.ResourceNotFoundException;
import org.example.jobseekerservice.dto.Skill.SkillCreationDTO;
import org.example.jobseekerservice.dto.Skill.SkillDTO;
import org.example.jobseekerservice.dto.Skill.SkillUpdateDTO;
import org.example.jobseekerservice.entity.Skill;
import org.example.jobseekerservice.mapper.SkillMapper;
import org.example.jobseekerservice.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final JobSeekerService jobSeekerService;
    private final SkillMapper skillMapper;

    public List<SkillDTO> getAllSkills() {
        List<Skill> skills = skillRepository.findAll();

        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SkillDTO> getJobSeekerSkills(String jobSeekerId) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.getJobSeekerById(jobSeekerId);

        List<Skill> skills = skillRepository.findByJobSeekerId(jobSeeker.getId());

        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    public SkillDTO getSkillById(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        return skillMapper.toDto(skill);
    }

    public SkillDTO createSkill(SkillCreationDTO request) {
        jobSeekerService.getJobSeekerById(request.getJobSeekerId());

        Skill skill = skillMapper.toEntity(request);
        Skill createdSkill = skillRepository.save(skill);

        return skillMapper.toDto(createdSkill);
    }

    public SkillDTO updateSkill(String id, SkillUpdateDTO request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        skillMapper.toEntity(request, skill);
        Skill updatedSkill = skillRepository.save(skill);

        return skillMapper.toDto(updatedSkill);
    }

    public void deleteSkill(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        skillRepository.delete(skill);
    }
}
