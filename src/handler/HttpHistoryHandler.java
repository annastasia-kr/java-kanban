package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.ServiceErrorResponse;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class HttpHistoryHandler extends BaseHttpHandler {
    public HttpHistoryHandler(TaskManager taskManager) {
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
                default: {
                    ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse(String.format("Обработка " +
                            "метода %s не предусмотрена", requestMethod), 405, exchange.getRequestURI().getPath());
                    String jsonText = jsonMapper.toJson(serviceErrorResponse);
                    sendText(exchange, jsonText, serviceErrorResponse.getErrorCode());
                }
            }
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
        if (Pattern.matches("^/history$", path)) {
            String response = jsonMapper.toJson(taskManager.getHistory());
            sendText(exchange, response, 200);
        }
    }
}
