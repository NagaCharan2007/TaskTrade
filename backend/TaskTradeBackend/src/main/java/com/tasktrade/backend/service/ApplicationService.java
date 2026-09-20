package com.tasktrade.backend.service;

import com.tasktrade.backend.dto.ApplicationRequest;
import com.tasktrade.backend.dto.ApplicationResponse;
import com.tasktrade.backend.entity.TaskApplication;
import com.tasktrade.backend.entity.Task;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.exception.BadRequestException;
import com.tasktrade.backend.exception.ResourceNotFoundException;
import com.tasktrade.backend.repository.TaskApplicationRepository;
import com.tasktrade.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final TaskApplicationRepository applicationRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public ApplicationResponse apply(User applicant, Long taskId, ApplicationRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        if (!"OPEN".equals(task.getStatus())) throw new BadRequestException("This task is not open");
        if (task.getRequester().getId().equals(applicant.getId())) {
            throw new BadRequestException("Task owners cannot apply to their own task");
        }
        if (applicationRepository.findByTaskIdAndApplicantId(taskId, applicant.getId()).isPresent()) {
            throw new BadRequestException("You have already applied to this task");
        }
        TaskApplication application = new TaskApplication();
        application.setTask(task);
        application.setApplicant(applicant);
        application.setMessage(request.getMessage());
        application.setStatus("PENDING");
        application.setAppliedAt(java.time.LocalDateTime.now());
        return ApplicationResponse.from(applicationRepository.save(application));
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> findForTask(User user, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        if (!task.getRequester().getId().equals(user.getId())) {
            throw new BadRequestException("Only the task owner can view applications");
        }
        return applicationRepository.findByTaskIdOrderByAppliedAtAsc(taskId).stream()
                .map(ApplicationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> findForApplicant(User applicant) {
        return applicationRepository.findByApplicantIdOrderByAppliedAtDesc(applicant.getId()).stream()
                .map(ApplicationResponse::from).toList();
    }

    @Transactional
    public ApplicationResponse accept(User owner, Long applicationId) {
        TaskApplication selected = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));
        Task task = selected.getTask();
        if (!task.getRequester().getId().equals(owner.getId())) {
            throw new BadRequestException("Only the task owner can accept an applicant");
        }
        if (!"OPEN".equals(task.getStatus())) throw new BadRequestException("This task is not open");
        selected.setStatus("ACCEPTED");
        task.setSelectedHelper(selected.getApplicant());
        task.setStatus("ACCEPTED");
        task.setUpdatedAt(java.time.LocalDateTime.now());
        applicationRepository.findByTaskIdAndStatus(task.getId(), "PENDING")
                .forEach(application -> application.setStatus("REJECTED"));
        taskRepository.save(task);
        return ApplicationResponse.from(applicationRepository.save(selected));
    }
}
