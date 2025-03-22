package org.example.jobseekerservice.repository;

import org.example.jobseekerservice.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, String> {
    List<Skill> findByJobSeekerId(String jobSeekerId);
}
