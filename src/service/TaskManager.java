package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<SubTask> getSubTaskList();

    void clearTaskMap();

    void clearEpicMap();

    void clearSubTaskMap();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    Task deleteTaskById(int id);

    Epic deleteEpicById(int id);

    SubTask deleteSubTaskById(int id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    SubTask updateSubTask(SubTask subTask);

    List<SubTask> getEpicSubTasks(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    int getIdCounter();
}
