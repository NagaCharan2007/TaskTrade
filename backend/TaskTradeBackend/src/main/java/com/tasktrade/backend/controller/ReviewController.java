package com.tasktrade.backend.controller;

import com.tasktrade.backend.dto.ReviewRequest;
import com.tasktrade.backend.dto.ReviewResponse;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.service.ReviewService;
import com.tasktrade.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(Authentication authentication, @Valid @RequestBody ReviewRequest request) {
        return reviewService.create(userService.findByEmail(authentication.getName()), request);
    }

    @GetMapping("/user/{userId}")
    public List<ReviewResponse> forUser(@PathVariable @Positive Long userId) {
        return reviewService.findForUser(userId);
    }

    @GetMapping("/given/{userId}")
    public List<ReviewResponse> givenByUser(@PathVariable @Positive Long userId, Authentication authentication) {
        User currentUser = userService.findByEmail(authentication.getName());
        if (!currentUser.getId().equals(userId)) {
            throw new com.tasktrade.backend.exception.BadRequestException("You can only view your own review history");
        }
        return reviewService.findByReviewer(userId);
    }
}
