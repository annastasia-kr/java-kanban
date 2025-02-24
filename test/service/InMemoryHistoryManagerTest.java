package service;

import enumirations.Status;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void add() {
        Task task = new Task("Задача", "Описание", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1));
        taskManager.createTask(task);

        taskManager.getTaskById(task.getId());
        task.setName("Новое имя задачи");
        taskManager.updateTask(task);

        assertNotNull(taskManager.getHistory());
        assertEquals(1, taskManager.getHistory().size());
        assertEquals("Задача",taskManager.getHistory().get(0).getName());
    }

    @Test
    void shouldBeRemoved() {
        Task task = new Task("Задача", "Описание", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1));
        taskManager.createTask(task);
        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 27));
        taskManager.createTask(task1);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 2, 1));
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        task.setName("Новое имя задачи");
        taskManager.updateTask(task);
        taskManager.getTaskById(task.getId());

        assertEquals(3, taskManager.getHistory().size());
        assertEquals("Новое имя задачи",taskManager.getHistory().get(2).getName());
    }

    @Test
    void shouldBeRemovedAllEpics() {
        Task task = new Task("Задача", "Описание", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 21));
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Task task1 = new Task("Задача2", "Описание2", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1));
        taskManager.createTask(task1);
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);

        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task1.getId());

        assertEquals(4, taskManager.getHistory().size());
        taskManager.clearEpicMap();
        assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    void shouldBeRemovedAllSubTasks() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача1", "Описание1", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), 1);
        taskManager.createSubTask(subTask1);
        SubTask subTask = new SubTask("Подзадача", "Описание", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 26), 1);
        taskManager.createSubTask(subTask);

        taskManager.getSubTaskById(subTask.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getEpicById(epic1.getId());

        assertEquals(4, taskManager.getHistory().size());
        taskManager.clearSubTaskMap();
        assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    void shouldBeDeletedSubTaskByIdFromHistory() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача1", "Описание1", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), 1);
        taskManager.createSubTask(subTask1);
        SubTask subTask = new SubTask("Подзадача", "Описание",  Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 2, 1), 1);
        taskManager.createSubTask(subTask);

        taskManager.getSubTaskById(subTask.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask1.getId());

        assertEquals(3, taskManager.getHistory().size());
        taskManager.deleteSubTaskById(subTask1.getId());
        assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    void shouldBeDeletedEpicByIdFromHistory() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача1", "Описание1",  Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), 1);
        taskManager.createSubTask(subTask1);
        SubTask subTask = new SubTask("Подзадача", "Описание",  Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 2, 1, 1), 1);
        taskManager.createSubTask(subTask);

        taskManager.getSubTaskById(subTask.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask1.getId());

        assertEquals(3, taskManager.getHistory().size());
        taskManager.deleteEpicById(epic.getId());
        assertEquals(0, taskManager.getHistory().size());
    }
}