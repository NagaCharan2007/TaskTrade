package com.tasktrade.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationRequest {
    @Size(max = 500, message = "Message must be at most 500 characters")
    private String message;
}
