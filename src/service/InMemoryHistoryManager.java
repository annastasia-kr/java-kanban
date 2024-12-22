package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>(10);
    }

    public InMemoryHistoryManager(ArrayList<Task> history) {
        this.history = new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() < 10) {
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
