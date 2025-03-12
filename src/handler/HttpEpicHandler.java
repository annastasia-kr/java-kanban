package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.ManagerTasksTimeIntersectionException;
import exception.NotFoundException;
import exception.ServiceErrorResponse;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HttpEpicHandler extends BaseHttpHandler {
    public HttpEpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case GET: {
                    handleGet(exchange, path);
                    break;
                }
                case DELETE: {
                    handleDelete(exchange, path);
                    break;
                }
                case POST: {
                    handlePost(exchange, path);
                    break;
                }
                default: {
                    ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(String.format("Обработка " +
                            "метода %s не предусмотрена", requestMethod), METHOD_NOT_ALLOWED_CODE, exchange.getRequestURI().getPath());
                    String jsonText = jsonMapper.toJson(serviceErrorResponse);
                    sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
                }
            }
        } catch (ManagerTasksTimeIntersectionException exception) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(exception.getMessage(), NOT_ACCEPTABLE_CODE,
                    exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        } catch (NotFoundException exception) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(exception.getMessage(), NOT_FOUND_CODE,
                    exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        } catch (Exception exception) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(exception.getMessage(), INTERNAL_SERVER_ERROR_CODE,
                    exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/epics$", path)) {
            String response = jsonMapper.toJson(taskManager.getEpicList());
            sendText(exchange, response, OK_CODE);
        }
        if (Pattern.matches("^/epics/\\d+$", path)) {
            String pathId = path.replaceFirst("/epics/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                Epic foundedEpic = taskManager.getEpicById(id);
                String response = jsonMapper.toJson(foundedEpic);
                sendText(exchange, response, OK_CODE);
            } else {
                ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse("Некорректный формат id",
                        NOT_FOUND_CODE, exchange.getRequestURI().getPath());
                String jsonText = jsonMapper.toJson(serviceErrorResponse);
                sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
            }
        }
        if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
            String pathId = path.replaceFirst("/epics/", "").replaceFirst("/subtasks", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                Epic foundedEpic = taskManager.getEpicById(id);
                String response = jsonMapper.toJson(taskManager.getEpicSubTasks(foundedEpic));
                sendText(exchange, response, OK_CODE);
            } else {
                ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse("Некорректный формат id",
                        NOT_FOUND_CODE, exchange.getRequestURI().getPath());
                String jsonText = jsonMapper.toJson(serviceErrorResponse);
                sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
            }
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/epics/\\d+$", path)) {
            String pathId = path.replaceFirst("/epics/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                Epic deletedEpic = taskManager.deleteEpicById(id);
                String response = jsonMapper.toJson(deletedEpic);
                sendText(exchange, response, OK_CODE);
            }
        } else {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse("Некорректный формат id",
                    NOT_FOUND_CODE, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        }
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/epics$", path)) {
            byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
            String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);

            Epic epicFromJson = jsonMapper.fromJson(bodyString, Epic.class);
            Epic epic = taskManager.createEpic(epicFromJson);

            String response = jsonMapper.toJson(epic);
            sendText(exchange, response, CREATED_CODE);
        }
    }
}
