package com.tasktrade.backend.service;

import com.tasktrade.backend.dto.ReviewRequest;
import com.tasktrade.backend.dto.ReviewResponse;
import com.tasktrade.backend.entity.Review;
import com.tasktrade.backend.entity.Task;
import com.tasktrade.backend.entity.TaskCompletion;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.exception.BadRequestException;
import com.tasktrade.backend.exception.ResourceNotFoundException;
import com.tasktrade.backend.repository.ReviewRepository;
import com.tasktrade.backend.repository.TaskCompletionRepository;
import com.tasktrade.backend.repository.TaskRepository;
import com.tasktrade.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final TaskRepository taskRepository;
    private final TaskCompletionRepository completionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse create(User reviewer, ReviewRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + request.getTaskId()));
        TaskCompletion completion = completionRepository.findByTaskId(task.getId())
                .orElseThrow(() -> new BadRequestException("Reviews require a completed task"));
        User reviewee;
        if (task.getRequester().getId().equals(reviewer.getId())) {
            reviewee = completion.getCompletedBy();
        } else if (completion.getCompletedBy().getId().equals(reviewer.getId())) {
            reviewee = task.getRequester();
        } else {
            throw new BadRequestException("Only users involved in the task can submit a review");
        }
        if (reviewRepository.existsByTaskIdAndReviewerId(task.getId(), reviewer.getId())) {
            throw new BadRequestException("You have already reviewed this task");
        }
        Review review = new Review();
        review.setTask(task);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(java.time.LocalDateTime.now());
        updateRating(reviewee, request.getRating());
        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> findForUser(Long userId) {
        if (!userRepository.existsById(userId)) throw new ResourceNotFoundException("User not found: " + userId);
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(userId).stream()
                .map(ReviewResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> findByReviewer(Long reviewerId) {
        if (!userRepository.existsById(reviewerId)) throw new ResourceNotFoundException("User not found: " + reviewerId);
        return reviewRepository.findByReviewerIdOrderByCreatedAtDesc(reviewerId).stream()
                .map(ReviewResponse::from).toList();
    }

    private void updateRating(User user, int rating) {
        long reviewCount = reviewRepository.countByRevieweeId(user.getId());
        java.math.BigDecimal current = user.getAverageRating();
        if (current == null || reviewCount == 0) {
            user.setAverageRating(java.math.BigDecimal.valueOf(rating));
        } else {
            user.setAverageRating(current.multiply(java.math.BigDecimal.valueOf(reviewCount))
                    .add(java.math.BigDecimal.valueOf(rating))
                    .divide(java.math.BigDecimal.valueOf(reviewCount + 1), 2, java.math.RoundingMode.HALF_UP));
        }
        userRepository.save(user);
    }
}
