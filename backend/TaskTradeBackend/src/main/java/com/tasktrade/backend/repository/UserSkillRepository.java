package com.tasktrade.backend.repository;

import com.tasktrade.backend.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserId(Long userId);
    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);
}
