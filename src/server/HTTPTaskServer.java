package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handler.*;
import service.Managers;
import service.TaskManager;
import typeadapter.DurationAdapter;
import typeadapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HTTPTaskServer {
    public static final int PORT = 8080;

    private final HttpServer server;

    private final TaskManager taskManager;

    public HTTPTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        this.server.createContext("/tasks", new HttpTaskHandler(taskManager));
        this.server.createContext("/subtasks", new HttpSubtaskHandler(taskManager));
        this.server.createContext("/epics", new HttpEpicHandler(taskManager));
        this.server.createContext("/history", new HttpHistoryHandler(taskManager));
        this.server.createContext("/prioritized", new HttpPrioritizedListHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        HTTPTaskServer taskServer = new HTTPTaskServer(Managers.getDefault());
        taskServer.start();
        taskServer.stop();
    }

    public void start() {
        System.out.println("Starter TaskServer " + PORT);
        System.out.println("http://localhost: " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Stopped TaskServer " + PORT);
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson;
    }
}
