package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.ServiceErrorResponse;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class HttpPrioritizedListHandler extends BaseHttpHandler {
    public HttpPrioritizedListHandler(TaskManager taskManager) {
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
                default: {
                    ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(String.format("Обработка " +
                            "метода %s не предусмотрена", requestMethod), METHOD_NOT_ALLOWED_CODE, exchange.getRequestURI().getPath());
                    String jsonText = jsonMapper.toJson(serviceErrorResponse);
                    sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
                }
            }
        } catch (Exception exception) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(exception.getMessage(), INTERNAL_SERVER_ERROR_CODE,
                    exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(serviceErrorResponse);
            sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/prioritized$", path)) {
            String response = jsonMapper.toJson(taskManager.getPrioritizedTasks());
            sendText(exchange, response, OK_CODE);
        }
    }
}
