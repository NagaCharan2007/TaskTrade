package com.tasktrade.backend.controller;

import com.tasktrade.backend.dto.ContactResponse;
import com.tasktrade.backend.dto.TaskRequest;
import com.tasktrade.backend.dto.TaskResponse;
import com.tasktrade.backend.service.CompletionService;
import com.tasktrade.backend.service.TaskService;
import com.tasktrade.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final CompletionService completionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(Authentication authentication, @Valid @RequestBody TaskRequest request) {
        return taskService.create(userService.findByEmail(authentication.getName()), request);
    }

    @GetMapping
    public List<TaskResponse> findAll() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable @Positive Long id) {
        return taskService.getResponse(id);
    }

    @PutMapping("/{id}")
    public TaskResponse update(Authentication authentication, @PathVariable @Positive Long id,
                               @Valid @RequestBody TaskRequest request) {
        return taskService.update(userService.findByEmail(authentication.getName()), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable @Positive Long id) {
        taskService.delete(userService.findByEmail(authentication.getName()), id);
    }

    @PostMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(Authentication authentication, @PathVariable @Positive Long id) {
        completionService.complete(userService.findByEmail(authentication.getName()), id);
    }

    @GetMapping("/{taskId}/contact")
    public ContactResponse getContact(Authentication authentication, @PathVariable @Positive Long taskId) {
        return taskService.getContact(userService.findByEmail(authentication.getName()), taskId);
    }
}
