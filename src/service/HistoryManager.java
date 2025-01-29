package service;

import model.Task;
import java.util.List;
import java.util.Map;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    void removeAll(Map<Integer, ? extends Task> map);

    List<Task> getHistory();

}
