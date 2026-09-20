package com.tasktrade.backend.service;

import com.tasktrade.backend.dto.AuthResponse;
import com.tasktrade.backend.dto.LoginRequest;
import com.tasktrade.backend.dto.RegisterRequest;
import com.tasktrade.backend.dto.UserResponse;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.exception.BadRequestException;
import com.tasktrade.backend.repository.UserRepository;
import com.tasktrade.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email already exists");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setBio(request.getBio());
        user.setProfileImage(request.getProfileImage());
        user.setRole("STUDENT");
        user.setAverageRating(null);
        user.setCompletedTasks(0);
        user.setCreatedAt(LocalDateTime.now());

        return toResponse(userRepository.save(user));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        return new AuthResponse(jwtService.generateToken(email), "Bearer", toResponse(user));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBio(),
                user.getProfileImage(),
                user.getRole(),
                user.getAverageRating(),
                user.getCompletedTasks(),
                user.getCreatedAt());
    }
}
