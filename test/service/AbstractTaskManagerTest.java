package service;

import enumirations.Status;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {

    T taskManager;

    @BeforeEach
    abstract void beforeEach();

    @Test
    void createTask() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        Task task = new Task(name, description,  Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1));

        taskManager.createTask(task);
        Task taskFromTaskManager = taskManager.getTaskById(task.getId());

        assertEquals(1, taskFromTaskManager.getId());
        assertEquals(Status.NEW, taskFromTaskManager.getStatus());
        assertEquals(name, taskFromTaskManager.getName());
        assertEquals(description, taskFromTaskManager.getDescription());
    }

    @Test
    void createEpic() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        ArrayList<Integer> subTasksId = new ArrayList<>();
        Epic epic = new Epic(name, description);

        Epic createdEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(name, description,  Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), createdEpic.getId());
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
    void shouldBeNewEpicStatus() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        Epic epic = new Epic(name, description);

        Epic createdEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(name, description,  Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), createdEpic.getId());
        taskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask(name, description,  Status.NEW, Duration.ZERO,
                LocalDateTime.of(1991, 1, 1, 1, 1), createdEpic.getId());
        taskManager.createSubTask(subTask1);

        assertEquals(Status.NEW, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void shouldBeInProgressEpicStatus() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        Epic epic = new Epic(name, description);

        Epic createdEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(name, description,  Status.IN_PROGRESS, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), createdEpic.getId());
        taskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask(name, description,  Status.IN_PROGRESS, Duration.ZERO,
                LocalDateTime.of(1991, 1, 1, 1, 1), createdEpic.getId());
        taskManager.createSubTask(subTask1);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void shouldBeInProgressEpicStatusWithoutSubtasksInProgress() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        Epic epic = new Epic(name, description);

        Epic createdEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(name, description,  Status.DONE, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), createdEpic.getId());
        taskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask(name, description,  Status.NEW, Duration.ZERO,
                LocalDateTime.of(1991, 1, 1, 1, 1), createdEpic.getId());
        taskManager.createSubTask(subTask1);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void shouldBeDoneEpicStatus() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        Epic epic = new Epic(name, description);

        Epic createdEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(name, description,  Status.DONE, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), createdEpic.getId());
        taskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask(name, description,  Status.DONE, Duration.ZERO,
                LocalDateTime.of(1991, 1, 1, 1, 1), createdEpic.getId());
        taskManager.createSubTask(subTask1);

        assertEquals(Status.DONE, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void createSubTask() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        SubTask subTask = new SubTask(name, description, Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), epic.getId());

        taskManager.createSubTask(subTask);
        SubTask subTaskFromTaskManager = taskManager.getSubTaskById(subTask.getId());

        assertEquals(2, subTaskFromTaskManager.getId());
        assertEquals(Status.NEW, subTaskFromTaskManager.getStatus());
        assertEquals(name, subTaskFromTaskManager.getName());
        assertEquals(description, subTaskFromTaskManager.getDescription());
        assertEquals(epic.getId(), subTaskFromTaskManager.getEpicId());
    }

    @Test
    void updateTask() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1));
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
        SubTask subTask = new SubTask("Подзадача 1", "Описание", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), epic.getId());
        taskManager.createSubTask(subTask);

        subTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask);

        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }


    @Test
    void getTaskList() {
        Task task = new Task("Задача", "Описание", Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1));
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
        SubTask subTask = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1), epic.getId());
        ArrayList<SubTask> subTaskList = new ArrayList<>();

        subTaskList.add(subTask);
        taskManager.createSubTask(subTask);

        assertEquals(subTaskList, taskManager.getSubTaskList());
    }

    @Test
    void calculateEpicStartTime() {
        initializeTaskManager();

        SubTask subTask = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, Duration.ofMinutes(12),
                LocalDateTime.of(2025, 3, 18, 19, 50), 1);
        taskManager.createSubTask(subTask);

        assertEquals(LocalDateTime.of(2020, 3, 18, 19, 57),
                taskManager.getEpicById(1).getStartTime());
    }

    @Test
    void calculateEpicDuration() {
        initializeTaskManager();

        SubTask subTask = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, Duration.ofMinutes(12),
                LocalDateTime.of(2020, 10, 18, 19, 50), 1);
        taskManager.createSubTask(subTask);

        assertEquals(Duration.ofMinutes(22), taskManager.getEpicById(1).getDuration());
    }

    @Test
    void calculateEpicEndTime() {
        initializeTaskManager();

        SubTask subTask = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, Duration.ofMinutes(5),
                LocalDateTime.of(2020, 3, 18, 19, 03), 1);
        taskManager.createSubTask(subTask);

        assertEquals(LocalDateTime.of(2020, 3, 18, 20, 7),
                taskManager.getEpicById(1).getEndTime());
    }

    @Test
    void calculateIntersection() {
        initializeTaskManager();

        SubTask subTask2 = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, Duration.ofMinutes(1),
                LocalDateTime.of(1990, 1, 1, 1, 15), 1);
        taskManager.createSubTask(subTask2);
        System.out.println(taskManager);

        assertThrows(RuntimeException.class, () -> {
            SubTask subTask = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, Duration.ofMinutes(50),
                    LocalDateTime.of(2020, 3, 18, 19, 00), 1);
            taskManager.createSubTask(subTask);
        });
        assertThrows(RuntimeException.class, () -> {
            SubTask subTask1 = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, Duration.ofMinutes(1),
                    LocalDateTime.of(1990, 1, 1, 1, 2), 1);
            taskManager.createSubTask(subTask1);
        });
    }

    @Test
    void calculateEpicStartTimeWithNullStartDateTimeOfSubtask() {

        Epic epic = new Epic("nameEpic", "descriptionEpic");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("Подзадача", "Описание", Status.IN_PROGRESS, Duration.ZERO,
                null, 1);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача1", "Описание1", Status.IN_PROGRESS, Duration.ofMinutes(1),
                LocalDateTime.of(1990, 1, 1, 1, 2), 1);
        taskManager.createSubTask(subTask2);

        assertEquals(LocalDateTime.of(1990, 1, 1, 1, 3),
                taskManager.getEpicById(1).getEndTime());
        assertEquals(LocalDateTime.of(1990, 1, 1, 1, 2),
                taskManager.getEpicById(1).getStartTime());
    }

    public void initializeTaskManager() {
        String nameTask = "Задача 1";
        String descriptionTask = "Описание задачи 1";
        String nameTask2 = "Задача 2";
        String descriptionTask2 = "Описание задачи 2";
        String nameEpic = "Эпик 1";
        String descriptionEpic = "Описание эпика 1";
        String nameSubTask = "Подзадача первого эпика 1";
        String descriptionSubTask = "Описание подзадачи 1";

        Epic epic = new Epic(nameEpic, descriptionEpic);
        Task task = new Task(nameTask, descriptionTask, Status.DONE, Duration.ofMinutes(1),
                LocalDateTime.of(1990, 1, 1, 1, 1));
        Task task2 = new Task(nameTask2, descriptionTask2, Status.NEW, Duration.ofMinutes(12),
                LocalDateTime.of(2000, 1, 12, 1, 10));

        Epic createdEpic = taskManager.createEpic(epic);
        taskManager.createTask(task);
        SubTask subTask = new SubTask(nameSubTask, descriptionSubTask, Status.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2020, 3, 18, 19, 57), createdEpic.getId());
        taskManager.createSubTask(subTask);
        taskManager.createTask(task2);
    }

}

