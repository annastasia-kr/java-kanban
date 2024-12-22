package service;

import enumirations.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void add() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        taskManager.createTask(task);

        taskManager.getTaskById(task.getId());
        task.setName("Новое имя задачи");
        taskManager.updateTask(task);

        assertNotNull(taskManager.getHistory());
        assertEquals(1, taskManager.getHistory().size());
        assertEquals("Задача",taskManager.getHistory().get(0).getName());
    }
}