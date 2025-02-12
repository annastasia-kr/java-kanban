package service;

import enumirations.Status;
import enumirations.Type;
import exceptions.ManagerFileInitializationException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void clearTaskMap() {
        super.clearTaskMap();
        save();
    }

    @Override
    public void clearEpicMap() {
        super.clearEpicMap();
        save();
    }

    @Override
    public void clearSubTaskMap() {
        super.clearSubTaskMap();
        save();
    }

    @Override
    public Task deleteTaskById(int id) {
        Task task = super.deleteTaskById(id);
        if (task != null) {
            save();
        }
        return task;
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic epic = super.deleteEpicById(id);
        if (epic != null) {
            save();
        }
        return epic;
    }

    @Override
    public SubTask deleteSubTaskById(int id) {
        SubTask subTask = super.deleteSubTaskById(id);
        if (subTask != null) {
            save();
        }
        return subTask;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        if (createdTask != null) {
            save();
        }
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        if (createdEpic != null) {
            save();
        }
        return createdEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask createdSubTask = super.createSubTask(subTask);
        if (createdSubTask != null) {
            save();
        }
        return createdSubTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask updatedSubTask = super.updateSubTask(subTask);
        save();
        return updatedSubTask;
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            fileWriter.write(getStringFromTasksList(getTaskList(), Type.TASK));
            fileWriter.write(getStringFromTasksList(getEpicList(), Type.EPIC));
            fileWriter.write(getStringFromTasksList(getSubTaskList(), Type.SUBTASK));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }
    }

    private String getStringFromTasksList(List<? extends Task> taskList, Type type) {
        StringBuilder line = new StringBuilder();

        for (Task task : taskList) {
            line.append(task.getId()).append(',');
            line.append(type.name()).append(',');
            line.append(task.getName()).append(',');
            line.append(task.getStatus()).append(',');
            line.append(task.getDescription());

            if (task instanceof SubTask) {
                SubTask subTask = (SubTask) task;
                line.append(',').append(subTask.getEpicId());
            }
            line.append('\n');
        }
    return line.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            List<String> allLines = Files.readAllLines(file.toPath());
            for (int i = 1; i < allLines.size(); i++) {
                fileBackedTaskManager.readTaskFromFile(allLines.get(i));
            }
            fileBackedTaskManager.updateEpicsSubTasksList();
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerFileInitializationException("Ошибка при чтении из файла " + e.getMessage());
        }
    }

    private void updateEpicsSubTasksList() {
        List<Epic> epicsList = getEpicList();
        for (SubTask subTask : getSubTaskList()) {
            if (epicsList.contains(subTask.getEpicId())) {
                Epic epic = epicMap.get(subTask.getEpicId());
                epic.addToSubTasksId(subTask.getId());
            }
        }
        for (Epic epic : epicsList) {
            epic.setStatus(calculateEpicStatus(epic));
        }
    }

    private void readTaskFromFile(String line) {
        String[] tasksFields = line.split(",");

        int id = Integer.parseInt(tasksFields[0]);
        if (this.idCounter < id) {
            this.idCounter = id + 1;
        }
        Type type = Type.valueOf(tasksFields[1]);
        String name = tasksFields[2];
        Status status = Status.valueOf(tasksFields[3]);
        String description = tasksFields[4];

        switch (type) {
            case TASK -> taskMap.put(id, new Task(name, description, status));
            case EPIC -> epicMap.put(id, new Epic(name, description));
            case SUBTASK -> {
                int epicId = Integer.parseInt(tasksFields[5]);
                subTaskMap.put(id, new SubTask(name, description, status, epicId));
            }
        }
    }
}
