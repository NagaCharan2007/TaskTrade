package com.tasktrade.backend.service;

import com.tasktrade.backend.dto.SkillResponse;
import com.tasktrade.backend.entity.Skill;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.entity.UserSkill;
import com.tasktrade.backend.exception.BadRequestException;
import com.tasktrade.backend.exception.ResourceNotFoundException;
import com.tasktrade.backend.repository.SkillRepository;
import com.tasktrade.backend.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    public List<SkillResponse> findAll() {
        return skillRepository.findAll().stream().map(SkillResponse::from).toList();
    }

    @Transactional
    public SkillResponse addToUser(User user, Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));
        if (userSkillRepository.findByUserIdAndSkillId(user.getId(), skillId).isPresent()) {
            throw new BadRequestException("Skill is already added to this profile");
        }
        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkillRepository.save(userSkill);
        return SkillResponse.from(skill);
    }

    @Transactional
    public void removeFromUser(User user, Long skillId) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(user.getId(), skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill is not on this profile"));
        userSkillRepository.delete(userSkill);
    }
}
