package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    public static final Integer OK_CODE = 200;
    public static final Integer CREATED_CODE = 201;
    public static final Integer NOT_FOUND_CODE = 404;
    public static final Integer METHOD_NOT_ALLOWED_CODE = 405;
    public static final Integer NOT_ACCEPTABLE_CODE = 406;
    public static final Integer INTERNAL_SERVER_ERROR_CODE = 500;

    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String POST = "POST";

    protected TaskManager taskManager;
    protected static Gson jsonMapper;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        jsonMapper = Managers.getGson();
    }

    protected void sendText(HttpExchange h, String text, Integer code) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, response.length);
        h.getResponseBody().write(response);
        h.close();
    }

    protected int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
