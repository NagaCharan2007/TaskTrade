package com.tasktrade.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private String profileImage;
    private String role;
    private BigDecimal averageRating;
    private Integer completedTasks;
    private LocalDateTime createdAt;
}
