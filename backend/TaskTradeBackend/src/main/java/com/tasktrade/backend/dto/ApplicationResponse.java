package com.tasktrade.backend.dto;

import com.tasktrade.backend.entity.TaskApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private String taskStatus;
    private String requesterName;
    private Long applicantId;
    private String applicantName;
    private String message;
    private String status;
    private LocalDateTime appliedAt;

    public static ApplicationResponse from(TaskApplication application) {
        return new ApplicationResponse(application.getId(), application.getTask().getId(),
            application.getTask().getTitle(), application.getTask().getStatus(),
            application.getTask().getRequester().getName(),
                application.getApplicant().getId(), application.getApplicant().getName(),
                application.getMessage(), application.getStatus(), application.getAppliedAt());
    }
}
