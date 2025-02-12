package service;

import enumirations.Status;
import model.Epic;
import model.SubTask;
import model.Task;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {

    TaskManager taskManager;

    abstract void createTask();

    abstract void createEpic();

    abstract void createSubTask();

    abstract void updateTask();

    abstract void updateEpic();

    abstract void updateSubTask();

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
        Task task = new Task(nameTask, descriptionTask, Status.DONE);
        Task task2 = new Task(nameTask2, descriptionTask2, Status.NEW);

        Epic createdEpic = taskManager.createEpic(epic);
        taskManager.createTask(task);
        SubTask subTask = new SubTask(nameSubTask, descriptionSubTask, Status.NEW, createdEpic.getId());
        taskManager.createSubTask(subTask);
        taskManager.createTask(task2);
    }
}

