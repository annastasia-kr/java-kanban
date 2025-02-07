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

class FileBackedTaskManagerTest {

    TaskManager fileBackedTaskManagerTest;
    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = Files.createTempFile("data", ".csv").toFile();
        fileBackedTaskManagerTest = new FileBackedTaskManager(file);
    }

    @Test
    void saveInFile() throws IOException {
        initFileBackedTaskManager();
        List<String> expectedResult = new ArrayList<>();
        List<String> allLines = Files.readAllLines(file.toPath());

        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("2,TASK,Задача 1,DONE,Описание задачи 1");
        expectedResult.add("4,TASK,Задача 2,NEW,Описание задачи 2");
        expectedResult.add("1,EPIC,Эпик 1,NEW,Описание эпика 1");
        expectedResult.add("3,SUBTASK,Подзадача первого эпика 1,NEW,Описание подзадачи 1,1");

        assertEquals(expectedResult, allLines);
        assertEquals(5, fileBackedTaskManagerTest.getIdCounter());

    }

    @Test
    void saveInFileEmptyFileBackedTaskManager() throws IOException {

        List<String> expectedResult = new ArrayList<>();
        List<String> allLines = Files.readAllLines(file.toPath());

        assertEquals(expectedResult, allLines);
        assertEquals(1, fileBackedTaskManagerTest.getIdCounter());
    }

    @Test
    void loadFromEmptyFile() {
        FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, fileBackedTaskManagerTest.getTaskList().size());
        assertEquals(0, fileBackedTaskManagerTest.getEpicList().size());
        assertEquals(0, fileBackedTaskManagerTest.getSubTaskList().size());
        assertEquals(1, fileBackedTaskManagerTest.getIdCounter());
    }

    @Test
    void loadFromNotEmptyFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        fileBackedTaskManagerTest = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, fileBackedTaskManagerTest.getTaskList().size());
        assertEquals(1, fileBackedTaskManagerTest.getEpicList().size());
        assertEquals(1, fileBackedTaskManagerTest.getSubTaskList().size());
    }

    @Test
    void idCounterAfterLoadFromFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        fileBackedTaskManagerTest = FileBackedTaskManager.loadFromFile(file);

        assertEquals(26, fileBackedTaskManagerTest.getIdCounter());
    }

    @Test
    void readTasksFromFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        fileBackedTaskManagerTest = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(fileBackedTaskManagerTest.getTaskById(20));
        assertEquals("Задача 1", fileBackedTaskManagerTest.getTaskById(20).getName());
        assertEquals("Описание задачи 1", fileBackedTaskManagerTest.getTaskById(20).getDescription());
        assertEquals(Status.DONE, fileBackedTaskManagerTest.getTaskById(20).getStatus());
        assertNotNull(fileBackedTaskManagerTest.getTaskById(10));
        assertEquals("Задача 2", fileBackedTaskManagerTest.getTaskById(10).getName());
        assertEquals("Описание задачи 2", fileBackedTaskManagerTest.getTaskById(10).getDescription());
        assertEquals(Status.NEW, fileBackedTaskManagerTest.getTaskById(10).getStatus());
    }

    @Test
    void readEpicFromFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        List<Integer> subTasksId = new ArrayList<>();
        subTasksId.add(3);
        fileBackedTaskManagerTest = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(fileBackedTaskManagerTest.getEpicById(25));
        assertEquals("Эпик 1", fileBackedTaskManagerTest.getEpicById(25).getName());
        assertEquals("Описание эпика 1", fileBackedTaskManagerTest.getEpicById(25).getDescription());
        assertEquals(Status.NEW, fileBackedTaskManagerTest.getEpicById(25).getStatus());
        assertEquals(subTasksId, fileBackedTaskManagerTest.getEpicById(25).getSubTasksId());
    }

    @Test
    void readSubTaskFromFile() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            initFile(fileWriter);
        } catch(IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + e.getMessage());
        }

        fileBackedTaskManagerTest = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(fileBackedTaskManagerTest.getSubTaskById(3));
        assertEquals("Подзадача первого эпика 1", fileBackedTaskManagerTest.getSubTaskById(3).getName());
        assertEquals("Описание подзадачи 1", fileBackedTaskManagerTest.getSubTaskById(3).getDescription());
        assertEquals(Status.NEW, fileBackedTaskManagerTest.getSubTaskById(3).getStatus());
        assertEquals(25, fileBackedTaskManagerTest.getSubTaskById(3).getEpicId());
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

        Epic createdEpic = fileBackedTaskManagerTest.createEpic(epic);
        fileBackedTaskManagerTest.createTask(task);
        SubTask subTask = new SubTask(nameSubTask, descriptionSubTask, Status.NEW, createdEpic.getId());
        fileBackedTaskManagerTest.createSubTask(subTask);
        fileBackedTaskManagerTest.createTask(task2);
        subTasksId.add(subTask.getId());
        Epic epicFromTaskManager = fileBackedTaskManagerTest.getEpicById(epic.getId());
        Task taskFromTaskManager = fileBackedTaskManagerTest.getTaskById(task.getId());

        assertEquals(1, epicFromTaskManager.getId());
        assertEquals(Status.NEW, epicFromTaskManager.getStatus());
        assertEquals(nameEpic, epicFromTaskManager.getName());
        assertEquals(descriptionEpic, epicFromTaskManager.getDescription());
        assertEquals(nameTask, taskFromTaskManager.getName());
        assertEquals(Status.DONE, taskFromTaskManager.getStatus());
        assertEquals(subTasksId, epicFromTaskManager.getSubTasksId());
        assertEquals(5, fileBackedTaskManagerTest.getIdCounter());
    }

    void initFileBackedTaskManager() {
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

        Epic createdEpic = fileBackedTaskManagerTest.createEpic(epic);
        fileBackedTaskManagerTest.createTask(task);
        SubTask subTask = new SubTask(nameSubTask, descriptionSubTask, Status.NEW, createdEpic.getId());
        fileBackedTaskManagerTest.createSubTask(subTask);
        fileBackedTaskManagerTest.createTask(task2);
    }

    void initFile(FileWriter fileWriter) throws IOException {
        fileWriter.write("id,type,name,status,description,epic\n");
        fileWriter.write("20,TASK,Задача 1,DONE,Описание задачи 1\n");
        fileWriter.write("10,TASK,Задача 2,NEW,Описание задачи 2\n");
        fileWriter.write("25,EPIC,Эпик 1,NEW,Описание эпика 1\n");
        fileWriter.write("3,SUBTASK,Подзадача первого эпика 1,NEW,Описание подзадачи 1,25\n");
    }
}