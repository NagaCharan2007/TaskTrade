package com.tasktrade.backend.service;

import com.tasktrade.backend.entity.Task;
import com.tasktrade.backend.entity.TaskCompletion;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.exception.BadRequestException;
import com.tasktrade.backend.exception.ResourceNotFoundException;
import com.tasktrade.backend.repository.TaskCompletionRepository;
import com.tasktrade.backend.repository.TaskRepository;
import com.tasktrade.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CompletionService {
    private final TaskRepository taskRepository;
    private final TaskCompletionRepository completionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void complete(User owner, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        if (!task.getRequester().getId().equals(owner.getId())) {
            throw new BadRequestException("Only the task owner can complete this task");
        }
        if (task.getSelectedHelper() == null || !"ACCEPTED".equals(task.getStatus())) {
            throw new BadRequestException("This task cannot be completed");
        }
        if (completionRepository.findByTaskId(taskId).isPresent()) {
            throw new BadRequestException("This task is already completed");
        }
        TaskCompletion completion = new TaskCompletion();
        completion.setTask(task);
        completion.setCompletedBy(task.getSelectedHelper());
        completion.setCompletedAt(LocalDateTime.now());
        completionRepository.save(completion);
        task.setStatus("COMPLETED");
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        User helper = task.getSelectedHelper();
        helper.setCompletedTasks((helper.getCompletedTasks() == null ? 0 : helper.getCompletedTasks()) + 1);
        userRepository.save(helper);
    }
}
