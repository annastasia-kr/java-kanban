package model;

import enumirations.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    TaskManager taskManager;
    Task taskFromTaskManager;
    Task taskFromTaskManagerForCompare;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        Task createdTask = taskManager.createTask(new Task("Задача", "ТЗ", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2025, 1, 1, 1, 1)));
        Task taskForCompare = taskManager.createTask(new Task("Задача", "ТЗ", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2026, 1, 1, 1, 1)));

        taskFromTaskManager = taskManager.getTaskById(createdTask.getId());
        taskFromTaskManagerForCompare = taskManager.getTaskById(taskForCompare.getId());
    }

    @Test
    void shouldBeNotEquals() {
        assertNotEquals(taskFromTaskManager, taskFromTaskManagerForCompare);
    }

    @Test
    void shouldBeEquals() {
        taskFromTaskManager.setId(taskFromTaskManagerForCompare.getId());

        assertEquals(taskFromTaskManager, taskFromTaskManagerForCompare);
    }

    @Test
    void getEndTime() {
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 11),
                taskManager.getTaskById(1).getEndTime());
    }
}