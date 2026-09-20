package com.tasktrade.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Bio must be at most 500 characters")
    private String bio;

    @Size(max = 500, message = "Profile image must be at most 500 characters")
    private String profileImage;
}
