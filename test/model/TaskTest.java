package model;

import enumirations.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    TaskManager taskManager;
    Task taskFromTaskManager;
    Task taskFromTaskManagerForCompare;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        Task createdTask = taskManager.createTask(new Task("Задача", "ТЗ", Status.NEW));
        Task taskForCompare = taskManager.createTask(new Task("Задача", "ТЗ", Status.NEW));

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
}