package com.tasktrade.backend.controller;

import com.tasktrade.backend.dto.UserResponse;
import com.tasktrade.backend.dto.UserUpdateRequest;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse currentUser(Authentication authentication) {
        return userService.toResponse(userService.findByEmail(authentication.getName()));
    }

    @PutMapping("/me")
    public UserResponse updateProfile(Authentication authentication,
                                      @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateProfile(authentication.getName(), request);
    }
}
