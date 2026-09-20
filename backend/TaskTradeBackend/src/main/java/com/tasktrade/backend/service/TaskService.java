package com.tasktrade.backend.service;

import com.tasktrade.backend.dto.ContactResponse;
import com.tasktrade.backend.dto.TaskRequest;
import com.tasktrade.backend.dto.TaskResponse;
import com.tasktrade.backend.entity.Task;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.exception.BadRequestException;
import com.tasktrade.backend.exception.ResourceNotFoundException;
import com.tasktrade.backend.repository.TaskRepository;
import com.tasktrade.backend.repository.TaskApplicationRepository;
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
        Task task = findById(taskId);
        
        if (task.getSelectedHelper() == null) {
            throw new BadRequestException("This task does not have an accepted helper yet");
        }
        
        boolean isRequester = task.getRequester().getId().equals(currentUser.getId());
        boolean isHelper = task.getSelectedHelper().getId().equals(currentUser.getId());
        
        if (!isRequester && !isHelper) {
            throw new BadRequestException("Only the task requester or accepted helper can access this contact information");
        }
        
        User contactUser = isRequester ? task.getSelectedHelper() : task.getRequester();
        return new ContactResponse(contactUser.getName(), contactUser.getEmail());
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
