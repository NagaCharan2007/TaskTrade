package com.tasktrade.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Task type is required")
    @Size(max = 30, message = "Task type must be at most 30 characters")
    private String taskType;

    @Size(max = 100, message = "Required skill must be at most 100 characters")
    private String skillRequired;
}
