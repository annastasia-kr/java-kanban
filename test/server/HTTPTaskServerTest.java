package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enumiration.Status;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskServerTest {
    private TaskManager taskManager = new InMemoryTaskManager();
    private HTTPTaskServer taskServer  = new HTTPTaskServer(taskManager);
    private final Gson gson = HTTPTaskServer.getGson();

    HTTPTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.clearTaskMap();
        taskManager.clearEpicMap();
        taskManager.clearSubTaskMap();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> subTasksFromManager = taskManager.getSubTaskList();

        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subTasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
        assertEquals(1, subTasksFromManager.get(0).getEpicId(), "Некорректный id эпика");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = taskManager.getEpicList();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Base", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetAllTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.createTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);

        assertEquals(actual, taskManager.getTaskList());
    }

    @Test
    public void testGetAllSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Type subtaskType = new TypeToken<ArrayList<SubTask>>() {}.getType();
        List<SubTask> actual = gson.fromJson(response.body(), subtaskType);

        assertEquals(actual, taskManager.getSubTaskList());
    }

    @Test
    public void testGetAllEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Type epicType = new TypeToken<ArrayList<Epic>>() {}.getType();
        List<Epic> actual = gson.fromJson(response.body(), epicType);

        assertEquals(actual, taskManager.getEpicList());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.createTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Task actual = gson.fromJson(response.body(), Task.class);

        assertEquals(actual, taskManager.getTaskById(1));
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        SubTask actual = gson.fromJson(response.body(), SubTask.class);

        assertEquals(actual, taskManager.getSubTaskById(2));
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Epic actual = gson.fromJson(response.body(), Epic.class);

        assertEquals(actual, taskManager.getEpicById(1));
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.createTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Task actual = gson.fromJson(response.body(), Task.class);

        assertEquals(actual, task);
        assertEquals(0, taskManager.getTaskList().size());
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        SubTask actual = gson.fromJson(response.body(), SubTask.class);

        assertEquals(actual, task);
        assertEquals(0, taskManager.getSubTaskList().size());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Epic actual = gson.fromJson(response.body(), Epic.class);

        assertEquals(actual, epic);
        assertEquals(0, taskManager.getEpicList().size());
    }

    @Test
    public void testDeleteNotFoundTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.createTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
        assertEquals(1, taskManager.getTaskList().size());
    }

    @Test
    public void testDeleteNotFoundSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/20");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
        assertEquals(1, taskManager.getSubTaskList().size());
    }

    @Test
    public void testDeleteNotFoundEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
        assertEquals(1, taskManager.getEpicList().size());
    }

    @Test
    public void testGetEpicSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);
        SubTask task1 = new SubTask("Test 2_1", "Testing task 2_1", Status.IN_PROGRESS, Duration.ofMinutes(5),
                LocalDateTime.now().plus(Duration.ofMinutes(20)), 1);
        taskManager.createSubTask(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type subTaskType = new TypeToken<ArrayList<SubTask>>() {}.getType();
        List<SubTask> actual = gson.fromJson(response.body(), subTaskType);

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(actual, taskManager.getEpicSubTasks(taskManager.getEpicById(1)));
    }

    @Test
    public void testAddTaskWithIntersection() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.createTask(task);
        Task task1 = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        String taskJson1 = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTaskList();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testAddSubTaskWithIntersection() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);
        SubTask task1 = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> subTasksFromManager = taskManager.getSubTaskList();

        assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.createTask(task);
        Task task1 = new Task("New test name", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        task1.setId(1);
        String taskJson1 = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTaskList();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("New test name", taskManager.getTaskById(1).getName());
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Base", "Epic description");
        taskManager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        taskManager.createSubTask(task);
        SubTask task1 = new SubTask("New test name", "Testing task 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), 1);
        task1.setId(2);
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> subTasksFromManager = taskManager.getSubTaskList();

        assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач");
        assertEquals("New test name", taskManager.getSubTaskById(2).getName());
    }
}