package service;

import enumirations.Status;
import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {

    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = Files.createTempFile("data", ".csv").toFile();
        taskManager = new FileBackedTaskManager(file);
    }

    @Override
    @Test
    void createTask() {
        String name = "Проект 1";
        String description = "Составить ТЗ для проекта 1";
        Task task = new Task(name, description, Status.NEW);
        List<String> expectedResult = new ArrayList<>();

        taskManager.createTask(task);
        List<String> allLines;
        try {
            allLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Task taskFromTaskManager = taskManager.getTaskById(task.getId());
        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("1,TASK,Проект 1,NEW,Составить ТЗ для проекта 1");


        assertEquals(expectedResult, allLines);
        assertEquals(2, taskManager.getIdCounter());
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
        List<String> expectedResult = new ArrayList<>();

        Epic createdEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(name, description, Status.NEW, createdEpic.getId());
        taskManager.createSubTask(subTask);
        subTasksId.add(subTask.getId());
        Epic epicFromTaskManager = taskManager.getEpicById(epic.getId());
        List<String> allLines;
        try {
            allLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("1,EPIC,Проект 1,NEW,Составить ТЗ для проекта 1");
        expectedResult.add("2,SUBTASK,Проект 1,NEW,Составить ТЗ для проекта 1,1");


        assertEquals(expectedResult, allLines);
        assertEquals(3, taskManager.getIdCounter());
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
        List<String> expectedResult = new ArrayList<>();

        taskManager.createSubTask(subTask);
        SubTask subTaskFromTaskManager = taskManager.getSubTaskById(subTask.getId());
        List<String> allLines;
        try {
            allLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("1,EPIC,Эпик,NEW,Описание");
        expectedResult.add("2,SUBTASK,Проект 1,NEW,Составить ТЗ для проекта 1,1");

        assertEquals(expectedResult, allLines);
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
        List<String> expectedResult = new ArrayList<>();

        task.setName("Новое имя задачи");
        taskManager.updateTask(task);
        List<String> allLines;
        try {
            allLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("1,TASK,Новое имя задачи,NEW,Описание задачи");

        assertEquals(expectedResult, allLines);
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Override
    @Test
    void updateEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        List<String> expectedResult = new ArrayList<>();

        epic.setName("Новое имя эпика");
        taskManager.updateEpic(epic);
        List<String> allLines;
        try {
            allLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("1,EPIC,Новое имя эпика,NEW,Описание эпика");

        assertEquals(expectedResult, allLines);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Override
    @Test
    void updateSubTask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1", "Описание", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask);
        List<String> expectedResult = new ArrayList<>();

        subTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask);
        List<String> allLines;
        try {
            allLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("1,EPIC,Эпик,IN_PROGRESS,Описание эпика");
        expectedResult.add("2,SUBTASK,Подзадача 1,IN_PROGRESS,Описание,1");

        assertEquals(expectedResult, allLines);
        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void saveInFile() throws IOException {
        initializeTaskManager();
        List<String> expectedResult = new ArrayList<>();
        List<String> allLines = Files.readAllLines(file.toPath());

        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("2,TASK,Задача 1,DONE,Описание задачи 1");
        expectedResult.add("4,TASK,Задача 2,NEW,Описание задачи 2");
        expectedResult.add("1,EPIC,Эпик 1,NEW,Описание эпика 1");
        expectedResult.add("3,SUBTASK,Подзадача первого эпика 1,NEW,Описание подзадачи 1,1");

        assertEquals(expectedResult, allLines);
        assertEquals(5, taskManager.getIdCounter());

    }

    @Test
    void saveInFileEmptyFileBackedTaskManager() throws IOException {

        List<String> expectedResult = new ArrayList<>();
        List<String> allLines = Files.readAllLines(file.toPath());

        assertEquals(expectedResult, allLines);
        assertEquals(1, taskManager.getIdCounter());
    }

    @Test
    void loadFromEmptyFile() {
        FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, taskManager.getTaskList().size());
        assertEquals(0, taskManager.getEpicList().size());
        assertEquals(0, taskManager.getSubTaskList().size());
        assertEquals(1, taskManager.getIdCounter());
    }

    @Test
    void loadFromNotEmptyFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initializeFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        taskManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, taskManager.getTaskList().size());
        assertEquals(1, taskManager.getEpicList().size());
        assertEquals(1, taskManager.getSubTaskList().size());
    }

    @Test
    void idCounterAfterLoadFromFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initializeFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        taskManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(26, taskManager.getIdCounter());
    }

    @Test
    void readTasksFromFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initializeFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        taskManager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(taskManager.getTaskById(20));
        assertEquals("Задача 1", taskManager.getTaskById(20).getName());
        assertEquals("Описание задачи 1", taskManager.getTaskById(20).getDescription());
        assertEquals(Status.DONE, taskManager.getTaskById(20).getStatus());
        assertNotNull(taskManager.getTaskById(10));
        assertEquals("Задача 2", taskManager.getTaskById(10).getName());
        assertEquals("Описание задачи 2", taskManager.getTaskById(10).getDescription());
        assertEquals(Status.NEW, taskManager.getTaskById(10).getStatus());
    }

    @Test
    void readEpicFromFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initializeFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        taskManager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(taskManager.getEpicById(25));
        assertEquals("Эпик 1", taskManager.getEpicById(25).getName());
        assertEquals("Описание эпика 1", taskManager.getEpicById(25).getDescription());
        assertEquals(Status.NEW, taskManager.getEpicById(25).getStatus());
    }

    @Test
    void readSubTaskFromFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initializeFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        taskManager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(taskManager.getSubTaskById(3));
        assertEquals("Подзадача первого эпика 1", taskManager.getSubTaskById(3).getName());
        assertEquals("Описание подзадачи 1", taskManager.getSubTaskById(3).getDescription());
        assertEquals(Status.NEW, taskManager.getSubTaskById(3).getStatus());
        assertEquals(25, taskManager.getSubTaskById(3).getEpicId());
    }

    @Test
    void createTasksOfEachTypes() {
        String nameTask = "Задача 1";
        String descriptionTask = "Описание задачи 1";
        String nameTask2 = "Задача 2";
        String descriptionTask2 = "Описание задачи 2";
        String nameEpic = "Эпик 1";
        String descriptionEpic = "Описание эпика 1";
        String nameSubTask = "Подзадача первого эпика 1";
        String descriptionSubTask = "Описание подзадачи 1";
        ArrayList<Integer> subTasksId = new ArrayList<>();
        Epic epic = new Epic(nameEpic, descriptionEpic);
        Task task = new Task(nameTask, descriptionTask, Status.DONE);
        Task task2 = new Task(nameTask2, descriptionTask2, Status.NEW);

        Epic createdEpic = taskManager.createEpic(epic);
        taskManager.createTask(task);
        SubTask subTask = new SubTask(nameSubTask, descriptionSubTask, Status.NEW, createdEpic.getId());
        taskManager.createSubTask(subTask);
        taskManager.createTask(task2);
        subTasksId.add(subTask.getId());
        Epic epicFromTaskManager = taskManager.getEpicById(epic.getId());
        Task taskFromTaskManager = taskManager.getTaskById(task.getId());

        assertEquals(1, epicFromTaskManager.getId());
        assertEquals(Status.NEW, epicFromTaskManager.getStatus());
        assertEquals(nameEpic, epicFromTaskManager.getName());
        assertEquals(descriptionEpic, epicFromTaskManager.getDescription());
        assertEquals(nameTask, taskFromTaskManager.getName());
        assertEquals(Status.DONE, taskFromTaskManager.getStatus());
        assertEquals(subTasksId, epicFromTaskManager.getSubTasksId());
        assertEquals(5, taskManager.getIdCounter());
    }

    private void initializeFile(FileWriter fileWriter) throws IOException {
        fileWriter.write("id,type,name,status,description,epic\n");
        fileWriter.write("20,TASK,Задача 1,DONE,Описание задачи 1\n");
        fileWriter.write("10,TASK,Задача 2,NEW,Описание задачи 2\n");
        fileWriter.write("25,EPIC,Эпик 1,NEW,Описание эпика 1\n");
        fileWriter.write("3,SUBTASK,Подзадача первого эпика 1,NEW,Описание подзадачи 1,25\n");
    }
}