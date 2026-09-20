package com.tasktrade.backend.dto;

import com.tasktrade.backend.entity.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillResponse {
    private Long id;
    private String name;
    private String description;

    public static SkillResponse from(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName(), skill.getDescription());
    }
}
