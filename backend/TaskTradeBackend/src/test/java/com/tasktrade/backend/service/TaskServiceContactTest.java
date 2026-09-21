package com.tasktrade.backend.service;

import com.tasktrade.backend.dto.ContactResponse;
import com.tasktrade.backend.entity.Task;
import com.tasktrade.backend.entity.TaskApplication;
import com.tasktrade.backend.entity.User;
import com.tasktrade.backend.exception.BadRequestException;
import com.tasktrade.backend.repository.TaskApplicationRepository;
import com.tasktrade.backend.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceContactTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskApplicationRepository applicationRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void requesterCanContactSpecificPendingApplicant() {
        User requester = newUser(1L, "Requester", "requester@test.com");
        User applicant = newUser(2L, "Alice", "alice@test.com");
        Task task = newTask(requester, null, "OPEN");

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        TaskApplication application = newApplication(task, applicant, "PENDING");
        when(applicationRepository.findByTaskIdAndApplicantId(10L, 2L)).thenReturn(Optional.of(application));

        ContactResponse response = taskService.getContact(requester, 10L, 2L);

        assertEquals("Alice", response.getName());
        assertEquals("alice@test.com", response.getEmail());
    }

    @Test
    void requesterCanContactSingleActiveApplicantWithoutApplicantId() {
        User requester = newUser(1L, "Requester", "requester@test.com");
        User applicant = newUser(2L, "Alice", "alice@test.com");
        Task task = newTask(requester, null, "OPEN");

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(applicationRepository.findByTaskIdOrderByAppliedAtAsc(10L))
                .thenReturn(List.of(newApplication(task, applicant, "PENDING")));

        ContactResponse response = taskService.getContact(requester, 10L);

        assertEquals("Alice", response.getName());
        assertEquals("alice@test.com", response.getEmail());
    }

    @Test
    void applicantCanContactRequesterWhileApplicationIsActive() {
        User requester = newUser(1L, "Requester", "requester@test.com");
        User applicant = newUser(2L, "Alice", "alice@test.com");
        Task task = newTask(requester, null, "OPEN");

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(applicationRepository.findByTaskIdAndApplicantId(10L, 2L))
                .thenReturn(Optional.of(newApplication(task, applicant, "PENDING")));

        ContactResponse response = taskService.getContact(applicant, 10L);

        assertEquals("Requester", response.getName());
        assertEquals("requester@test.com", response.getEmail());
    }

    @Test
    void acceptedHelperCanStillContactRequester() {
        User requester = newUser(1L, "Requester", "requester@test.com");
        User helper = newUser(2L, "Helper", "helper@test.com");
        Task task = newTask(requester, helper, "ACCEPTED");

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        ContactResponse response = taskService.getContact(helper, 10L);

        assertEquals("Requester", response.getName());
        assertEquals("requester@test.com", response.getEmail());
    }

    @Test
    void rejectedApplicationCannotContact() {
        User requester = newUser(1L, "Requester", "requester@test.com");
        User applicant = newUser(2L, "Alice", "alice@test.com");
        Task task = newTask(requester, null, "OPEN");

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(applicationRepository.findByTaskIdAndApplicantId(10L, 2L))
                .thenReturn(Optional.of(newApplication(task, applicant, "REJECTED")));

        assertThrows(BadRequestException.class, () -> taskService.getContact(applicant, 10L));
    }

    private User newUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Task newTask(User requester, User selectedHelper, String status) {
        Task task = new Task();
        task.setId(10L);
        task.setRequester(requester);
        task.setSelectedHelper(selectedHelper);
        task.setStatus(status);
        return task;
    }

    private TaskApplication newApplication(Task task, User applicant, String status) {
        TaskApplication application = new TaskApplication();
        application.setTask(task);
        application.setApplicant(applicant);
        application.setStatus(status);
        return application;
    }
}
