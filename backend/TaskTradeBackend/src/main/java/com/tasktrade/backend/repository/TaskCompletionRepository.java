package com.tasktrade.backend.repository;

import com.tasktrade.backend.entity.TaskCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskCompletionRepository extends JpaRepository<TaskCompletion, Long> {
    Optional<TaskCompletion> findByTaskId(Long taskId);
}
