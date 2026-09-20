package com.tasktrade.backend.repository;

import com.tasktrade.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
