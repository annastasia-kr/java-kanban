package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import typeadapter.DurationAdapter;
import typeadapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Managers {
    private Managers() {

    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson;
    }

}
