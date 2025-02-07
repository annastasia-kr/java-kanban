package service;

import enumirations.Status;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void getTaskList() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        ArrayList<Task> taskList = new ArrayList<>();

        taskList.add(task);
        taskManager.createTask(task);

        assertEquals(taskList, taskManager.getTaskList());
    }

    @Test
    void getEpicList() {
        Epic epic = new Epic("Эпик", "Описание");
        ArrayList<Epic> epicList = new ArrayList<>();

        epicList.add(epic);
        taskManager.createEpic(epic);

        assertEquals(epicList, taskManager.getEpicList());
    }

    @Test
    void getSubTaskList() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, epic.getId());
        ArrayList<SubTask> subTaskList = new ArrayList<>();

        subTaskList.add(subTask);
        taskManager.createSubTask(subTask);

        assertEquals(subTaskList, taskManager.getSubTaskList());
    }

    @Test
    void createTask() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        Task task = new Task(name, description, Status.NEW);

        taskManager.createTask(task);
        Task taskFromTaskManager = taskManager.getTaskById(task.getId());

        assertEquals(1, taskFromTaskManager.getId());
        assertEquals(Status.NEW, taskFromTaskManager.getStatus());
        assertEquals(name, taskFromTaskManager.getName());
        assertEquals(description, taskFromTaskManager.getDescription());
    }

    @Test
    void createTaskWithId() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        Task task = new Task(name, description, Status.NEW);

        taskManager.createTask(task, 10);
        Task taskFromTaskManager = taskManager.getTaskById(task.getId());

        assertEquals(10, taskFromTaskManager.getId());
        assertEquals(Status.NEW, taskFromTaskManager.getStatus());
        assertEquals(name, taskFromTaskManager.getName());
        assertEquals(description, taskFromTaskManager.getDescription());
        assertEquals(11, taskManager.getIdCounter());
    }

    @Test
    void createEpic() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        ArrayList<Integer> subTasksId = new ArrayList<>();
        Epic epic = new Epic(name, description);

        Epic createdEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(name, description, Status.NEW, createdEpic.getId());
        taskManager.createSubTask(subTask);
        subTasksId.add(subTask.getId());
        Epic epicFromTaskManager = taskManager.getEpicById(epic.getId());

        assertEquals(1, epicFromTaskManager.getId());
        assertEquals(Status.NEW, epicFromTaskManager.getStatus());
        assertEquals(name, epicFromTaskManager.getName());
        assertEquals(description, epicFromTaskManager.getDescription());
        assertEquals(subTasksId, epicFromTaskManager.getSubTasksId());
    }

    @Test
    void createEpicWithId() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        ArrayList<Integer> subTasksId = new ArrayList<>();
        Epic epic = new Epic(name, description);

        Epic createdEpic = taskManager.createEpic(epic, 12);
        SubTask subTask = new SubTask(name, description, Status.NEW, createdEpic.getId());
        taskManager.createSubTask(subTask, 5);
        subTasksId.add(subTask.getId());
        Epic epicFromTaskManager = taskManager.getEpicById(epic.getId());

        assertEquals(12, epicFromTaskManager.getId());
        assertEquals(Status.NEW, epicFromTaskManager.getStatus());
        assertEquals(name, epicFromTaskManager.getName());
        assertEquals(description, epicFromTaskManager.getDescription());
        assertEquals(subTasksId, epicFromTaskManager.getSubTasksId());
        assertEquals(13, taskManager.getIdCounter());
    }

    @Test
    void createSubTask() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        SubTask subTask = new SubTask(name, description, Status.NEW, epic.getId());

        taskManager.createSubTask(subTask);
        SubTask subTaskFromTaskManager = taskManager.getSubTaskById(subTask.getId());

        assertEquals(2, subTaskFromTaskManager.getId());
        assertEquals(Status.NEW, subTaskFromTaskManager.getStatus());
        assertEquals(name, subTaskFromTaskManager.getName());
        assertEquals(description, subTaskFromTaskManager.getDescription());
        assertEquals(epic.getId(), subTaskFromTaskManager.getEpicId());
    }

    @Test
    void createSubTaskWithId() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic, 1);
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        SubTask subTask = new SubTask(name, description, Status.NEW, epic.getId());

        taskManager.createSubTask(subTask, 2);
        SubTask subTaskFromTaskManager = taskManager.getSubTaskById(subTask.getId());

        assertEquals(2, subTaskFromTaskManager.getId());
        assertEquals(Status.NEW, subTaskFromTaskManager.getStatus());
        assertEquals(name, subTaskFromTaskManager.getName());
        assertEquals(description, subTaskFromTaskManager.getDescription());
        assertEquals(epic.getId(), subTaskFromTaskManager.getEpicId());
        assertEquals(3, taskManager.getIdCounter());
    }

    @Test
    void createdSubTaskIsNull() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic, 1);
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        SubTask subTask = new SubTask(name, description, Status.NEW, epic.getId());

        taskManager.createSubTask(subTask, 1);
        SubTask subTaskFromTaskManager = taskManager.getSubTaskById(subTask.getId());

        assertNull(subTaskFromTaskManager);
        assertEquals(2, taskManager.getIdCounter());
    }
    @Test
    void updateTask() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(task);

        task.setName("Новое имя задачи");
        taskManager.updateTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);

        epic.setName("Новое имя эпика");
        taskManager.updateEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void updateSubTask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1", "Описание", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask);

        subTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask);

        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }
}