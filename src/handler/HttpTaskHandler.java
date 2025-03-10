package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.ManagerTasksTimeIntersectionException;
import exception.NotFoundException;
import exception.ServiceErrorResponse;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HttpTaskHandler extends BaseHttpHandler {
    public HttpTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    handleGet(exchange, path);
                    break;
                }
                case "DELETE": {
                    handleDelete(exchange, path);
                    break;
                }
                case "POST": {
                    handlePost(exchange, path);
                    break;
                }
                default: {
                    ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(String.format("Обработка " +
                            "метода %s не предусмотрена", requestMethod), 405, exchange.getRequestURI().getPath());
                    String jsonText = jsonMapper.toJson(serviceErrorResponse);
                    sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
                }
            }
        } catch (NotFoundException exception) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(exception.getMessage(), 404,
                    exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        } catch (ManagerTasksTimeIntersectionException exception) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(exception.getMessage(), 406,
                    exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        } catch (Exception exception) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(exception.getMessage(), 500,
                    exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/tasks$", path)) {
            String response = jsonMapper.toJson(taskManager.getTaskList());
            sendText(exchange, response, 200);
        }
        if (Pattern.matches("^/tasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                Task foundedTask = taskManager.getTaskById(id);
                String response = jsonMapper.toJson(foundedTask);
                sendText(exchange, response, 200);
            } else {
                ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse("Некорректный формат id",
                        404, exchange.getRequestURI().getPath());
                String jsonText = jsonMapper.toJson(serviceErrorResponse);
                sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
            }
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/tasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                Task deletedTask = taskManager.deleteTaskById(id);
                String response = jsonMapper.toJson(deletedTask);
                sendText(exchange, response, 200);
            }
        } else {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse("Некорректный формат id",
                    404, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        }
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/tasks$", path)) {
            byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
            String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);

            Task taskFromJson = jsonMapper.fromJson(bodyString, Task.class);
            Task task;
            if (taskFromJson.getId() == 0) {
                task = taskManager.createTask(taskFromJson);
            } else {
                task = taskManager.updateTask(taskFromJson);
            }
            String response = jsonMapper.toJson(task);
            sendText(exchange, response, 201);
        }
    }
}




