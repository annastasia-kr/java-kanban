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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {

    File file;

    @Override
    @BeforeEach
    void beforeEach() {
        try {
            file = Files.createTempFile("data", ".csv").toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void saveInFile() throws IOException {
        initializeTaskManager();
        List<String> expectedResult = new ArrayList<>();
        List<String> allLines = Files.readAllLines(file.toPath());

        expectedResult.add("id,type,name,status,description,duration,startTime,epic");
        expectedResult.add("2,TASK,Задача 1,DONE,Описание задачи 1,PT1M,1990-01-01T01:01");
        expectedResult.add("4,TASK,Задача 2,NEW,Описание задачи 2,PT12M,2000-01-12T01:10");
        expectedResult.add("1,EPIC,Эпик 1,NEW,Описание эпика 1,PT10M,2020-03-18T19:57");
        expectedResult.add("3,SUBTASK,Подзадача первого эпика 1,NEW,Описание подзадачи 1,PT10M,2020-03-18T19:57,1");

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
        assertEquals(Duration.ofMinutes(57), taskManager.getTaskById(20).getDuration());
        assertEquals(LocalDateTime.of(1990, 1, 1, 1, 1),
                taskManager.getTaskById(20).getStartTime());
        assertNotNull(taskManager.getTaskById(10));
        assertEquals("Задача 2", taskManager.getTaskById(10).getName());
        assertEquals("Описание задачи 2", taskManager.getTaskById(10).getDescription());
        assertEquals(Status.NEW, taskManager.getTaskById(10).getStatus());
        assertEquals(Duration.ZERO, taskManager.getTaskById(10).getDuration());
        assertEquals(LocalDateTime.of(1997, 2, 5, 8, 1),
                taskManager.getTaskById(10).getStartTime());
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
        assertEquals(Duration.ofMinutes(9), taskManager.getEpicById(25).getDuration());
        assertEquals(LocalDateTime.of(1998, 1, 1, 1, 1),
                taskManager.getEpicById(25).getStartTime());
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
        assertEquals(Duration.ofMinutes(9), taskManager.getSubTaskById(3).getDuration());
        assertEquals(LocalDateTime.of(1998, 1, 1, 1, 1),
                taskManager.getSubTaskById(3).getStartTime());
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
        Task task = new Task(nameTask, descriptionTask, Status.DONE, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 1));
        Task task2 = new Task(nameTask2, descriptionTask2, Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 1, 26));

        Epic createdEpic = taskManager.createEpic(epic);
        taskManager.createTask(task);
        SubTask subTask = new SubTask(nameSubTask, descriptionSubTask, Status.NEW, Duration.ZERO,
                LocalDateTime.of(1990, 1, 1, 2, 3), createdEpic.getId());
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

    @Test
    void saveInFileTaskWhereStartTimeIsNull() throws IOException {
        String nameTask = "Задача 1";
        String descriptionTask = "Описание задачи 1";
        Task task = new Task(nameTask, descriptionTask, Status.DONE, Duration.ZERO, null);

        taskManager.createTask(task);
        List<String> expectedResult = new ArrayList<>();
        List<String> allLines = Files.readAllLines(file.toPath());

        expectedResult.add("id,type,name,status,description,duration,startTime,epic");
        expectedResult.add("1,TASK,Задача 1,DONE,Описание задачи 1,PT0S,null");

        assertEquals(expectedResult, allLines);
        assertEquals(2, taskManager.getIdCounter());

    }

    @Test
    void readTaskWhereStartTimeIsNull() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,duration,startTime,epic\n");
            fileWriter.write("5,TASK,Задача 1,DONE,Описание задачи 1,PT57M,null\n");
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        taskManager = FileBackedTaskManager.loadFromFile(file);

        assertNull(taskManager.getTaskById(5).getStartTime());
        assertNotNull(taskManager.getTaskById(5));
    }

    private void initializeFile(FileWriter fileWriter) throws IOException {
        fileWriter.write("id,type,name,status,description,duration,startTime,epic\n");
        fileWriter.write("20,TASK,Задача 1,DONE,Описание задачи 1,PT57M,1990-01-01T01:01\n");
        fileWriter.write("10,TASK,Задача 2,NEW,Описание задачи 2,PT0S,1997-02-05T08:01\n");
        fileWriter.write("25,EPIC,Эпик 1,NEW,Описание эпика 1,PT89M,2000-01-01T01:01\n");
        fileWriter.write("3,SUBTASK,Подзадача первого эпика 1,NEW,Описание подзадачи 1,PT9M,1998-01-01T01:01,25\n");
    }
}