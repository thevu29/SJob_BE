package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.Skill.SkillDTO;
import org.example.jobseekerservice.dto.Skill.request.CreateSkillRequest;
import org.example.jobseekerservice.dto.Skill.request.UpdateSkillRequest;
import org.example.jobseekerservice.entity.Skill;
import org.example.jobseekerservice.exception.ResourceNotFoundException;
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

    public SkillDTO getSkillById(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        return skillMapper.toDto(skill);
    }

    public SkillDTO createSkill(CreateSkillRequest request) {
        jobSeekerService.getJobSeekerById(request.getJobSeekerId());

        Skill skill = skillMapper.toEntity(request);
        Skill createdSkill = skillRepository.save(skill);

        return skillMapper.toDto(createdSkill);
    }

    public SkillDTO updateSkill(String id, UpdateSkillRequest request) {
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
