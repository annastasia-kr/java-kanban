package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;

    private List<Task> history;

    public InMemoryHistoryManager() {this.history = new LinkedList<>();}

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() < MAX_HISTORY_SIZE) {
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {return new LinkedList<>(history);}
}
