package com.tasktrade.backend.service;

import com.tasktrade.backend.dto.ContactResponse;
import com.tasktrade.backend.dto.TaskRequest;
import com.tasktrade.backend.dto.TaskResponse;
import com.tasktrade.backend.entity.Task;
import com.tasktrade.backend.entity.TaskApplication;
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
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskApplicationRepository applicationRepository;

    public List<TaskResponse> findAll() {
        return taskRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    public TaskResponse getResponse(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public TaskResponse create(User requester, TaskRequest request) {
        Task task = new Task();
        task.setRequester(requester);
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription().trim());
        task.setTaskType(request.getTaskType().trim());
        task.setSkillRequired(request.getSkillRequired());
        task.setStatus("OPEN");
        task.setCreatedAt(java.time.LocalDateTime.now());
        task.setUpdatedAt(task.getCreatedAt());
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(User requester, Long id, TaskRequest request) {
        Task task = findById(id);
        requireOwner(task, requester);
        if (!"OPEN".equals(task.getStatus())) throw new BadRequestException("Only open tasks can be updated");
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription().trim());
        task.setTaskType(request.getTaskType().trim());
        task.setSkillRequired(request.getSkillRequired());
        task.setUpdatedAt(java.time.LocalDateTime.now());
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void delete(User requester, Long id) {
        Task task = findById(id);
        requireOwner(task, requester);
        if (!"OPEN".equals(task.getStatus())) throw new BadRequestException("Only open tasks can be deleted");
        if (applicationRepository.existsByTaskId(id)) {
            throw new BadRequestException("Tasks with applications cannot be deleted");
        }
        taskRepository.delete(task);
    }

    public ContactResponse getContact(User currentUser, Long taskId) {
        return getContact(currentUser, taskId, null);
    }

    public ContactResponse getContact(User currentUser, Long taskId, Long applicantId) {
        Task task = findById(taskId);

        if (task.getRequester().getId().equals(currentUser.getId())) {
            User applicantContact = resolveRequesterContact(task, applicantId);
            return toContactResponse(applicantContact);
        }

        if (task.getSelectedHelper() != null && task.getSelectedHelper().getId().equals(currentUser.getId())) {
            return toContactResponse(task.getRequester());
        }

        TaskApplication activeApplication = applicationRepository.findByTaskIdAndApplicantId(taskId, currentUser.getId())
                .filter(this::isActiveApplication)
                .orElse(null);

        if (activeApplication != null) {
            return toContactResponse(task.getRequester());
        }

        throw new BadRequestException("You are not allowed to contact anyone for this task");
    }

    private User resolveRequesterContact(Task task, Long applicantId) {
        if (task.getSelectedHelper() != null) {
            return task.getSelectedHelper();
        }

        if (applicantId != null) {
            TaskApplication application = applicationRepository.findByTaskIdAndApplicantId(task.getId(), applicantId)
                    .orElseThrow(() -> new BadRequestException("No active application exists for that applicant"));
            if (!isActiveApplication(application)) {
                throw new BadRequestException("That application is not active");
            }
            return application.getApplicant();
        }

        List<TaskApplication> activeApplications = applicationRepository.findByTaskIdOrderByAppliedAtAsc(task.getId())
                .stream()
                .filter(this::isActiveApplication)
                .toList();

        if (activeApplications.isEmpty()) {
            throw new BadRequestException("There is no active applicant to contact for this task");
        }
        if (activeApplications.size() > 1) {
            throw new BadRequestException("Multiple active applicants exist for this task; please specify an applicantId");
        }
        return activeApplications.get(0).getApplicant();
    }

    private ContactResponse toContactResponse(User user) {
        return new ContactResponse(user.getName(), user.getEmail());
    }

    private boolean isActiveApplication(TaskApplication application) {
        return application != null && ("PENDING".equals(application.getStatus()) || "ACCEPTED".equals(application.getStatus()));
    }

    private void requireOwner(Task task, User user) {
        if (!task.getRequester().getId().equals(user.getId())) {
            throw new BadRequestException("Only the task owner can modify this task");
        }
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getRequester().getId(), task.getRequester().getName(),
                task.getSelectedHelper() == null ? null : task.getSelectedHelper().getId(),
            task.getSelectedHelper() == null ? null : task.getSelectedHelper().getName(),
                task.getTitle(), task.getDescription(), task.getTaskType(),
                task.getSkillRequired(), task.getStatus(), task.getCreatedAt(), task.getUpdatedAt());
    }
}
