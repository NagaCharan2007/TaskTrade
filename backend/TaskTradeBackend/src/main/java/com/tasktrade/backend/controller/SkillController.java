package com.tasktrade.backend.controller;

import com.tasktrade.backend.dto.SkillResponse;
import com.tasktrade.backend.service.SkillService;
import com.tasktrade.backend.service.UserService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Validated
public class SkillController {
    private final SkillService skillService;
    private final UserService userService;

    @GetMapping
    public List<SkillResponse> getSkills() {
        return skillService.findAll();
    }

    @PostMapping("/me/{skillId}")
    public SkillResponse addSkill(Authentication authentication, @PathVariable @Positive Long skillId) {
        return skillService.addToUser(userService.findByEmail(authentication.getName()), skillId);
    }

    @DeleteMapping("/me/{skillId}")
    public void removeSkill(Authentication authentication, @PathVariable @Positive Long skillId) {
        skillService.removeFromUser(userService.findByEmail(authentication.getName()), skillId);
    }
}
