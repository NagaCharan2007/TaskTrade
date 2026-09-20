package com.tasktrade.backend.dto;

import com.tasktrade.backend.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long reviewerId;
    private String reviewerName;
    private Long revieweeId;
    private String revieweeName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review.getId(), review.getTask().getId(), review.getTask().getTitle(),
                review.getReviewer().getId(), review.getReviewer().getName(),
                review.getReviewee().getId(), review.getReviewee().getName(),
                review.getRating(), review.getComment(), review.getCreatedAt());
    }
}
