package com.tasktrade.backend.repository;

import com.tasktrade.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRevieweeIdOrderByCreatedAtDesc(Long revieweeId);
    List<Review> findByReviewerIdOrderByCreatedAtDesc(Long reviewerId);
    boolean existsByTaskIdAndReviewerId(Long taskId, Long reviewerId);
    long countByRevieweeId(Long revieweeId);
}
