package service;

import model.Task;
import java.util.List;
import java.util.Set;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    void removeAll(Set<Integer> tasksId);
    List<Task> getHistory();
}
