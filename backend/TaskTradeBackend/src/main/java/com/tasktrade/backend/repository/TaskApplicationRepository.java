package com.tasktrade.backend.repository;

import com.tasktrade.backend.entity.TaskApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskApplicationRepository extends JpaRepository<TaskApplication, Long> {
    List<TaskApplication> findByTaskIdOrderByAppliedAtAsc(Long taskId);
    List<TaskApplication> findByApplicantIdOrderByAppliedAtDesc(Long applicantId);
    Optional<TaskApplication> findByTaskIdAndApplicantId(Long taskId, Long applicantId);
    List<TaskApplication> findByTaskIdAndStatus(Long taskId, String status);
    boolean existsByTaskId(Long taskId);
}
