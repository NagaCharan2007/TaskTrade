package com.tasktrade.backend.controller;

import com.tasktrade.backend.dto.ApplicationRequest;
import com.tasktrade.backend.dto.ApplicationResponse;
import com.tasktrade.backend.service.ApplicationService;
import com.tasktrade.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Validated
public class ApplicationController {
    private final ApplicationService applicationService;
    private final UserService userService;

    @PostMapping("/task/{taskId}")
    public ApplicationResponse apply(Authentication authentication, @PathVariable @Positive Long taskId,
                                     @Valid @RequestBody ApplicationRequest request) {
        return applicationService.apply(userService.findByEmail(authentication.getName()), taskId, request);
    }

    @GetMapping("/task/{taskId}")
    public List<ApplicationResponse> forTask(Authentication authentication,
                                              @PathVariable @Positive Long taskId) {
        return applicationService.findForTask(userService.findByEmail(authentication.getName()), taskId);
    }

    @GetMapping("/my")
    public List<ApplicationResponse> forApplicant(Authentication authentication) {
        return applicationService.findForApplicant(userService.findByEmail(authentication.getName()));
    }

    @PutMapping("/{applicationId}/accept")
    public ApplicationResponse accept(Authentication authentication,
                                      @PathVariable @Positive Long applicationId) {
        return applicationService.accept(userService.findByEmail(authentication.getName()), applicationId);
    }
}
