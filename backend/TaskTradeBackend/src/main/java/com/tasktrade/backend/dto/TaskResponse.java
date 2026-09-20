package com.tasktrade.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private Long selectedHelperId;
    private String selectedHelperName;
    private String title;
    private String description;
    private String taskType;
    private String skillRequired;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
