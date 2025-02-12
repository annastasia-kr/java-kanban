package service;

import enumirations.Status;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends AbstractTaskManagerTest <InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    @Test
    void updateTask() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(task);

        task.setName("Новое имя задачи");
        taskManager.updateTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Override
    @Test
    void updateEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);

        epic.setName("Новое имя эпика");
        taskManager.updateEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Override
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
}