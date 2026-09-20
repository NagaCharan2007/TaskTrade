package com.tasktrade.backend.service;

import com.tasktrade.backend.dto.UserResponse;
import com.tasktrade.backend.dto.UserUpdateRequest;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.exception.ResourceNotFoundException;
import com.tasktrade.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public UserResponse updateProfile(String email, UserUpdateRequest request) {
        User user = findByEmail(email);
        if (request.getName() != null) user.setName(request.getName().trim());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());
        return toResponse(userRepository.save(user));
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getBio(),
                user.getProfileImage(), user.getRole(), user.getAverageRating(),
                user.getCompletedTasks(), user.getCreatedAt());
    }
}
