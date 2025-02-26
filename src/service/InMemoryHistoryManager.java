package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> historyId;

    public InMemoryHistoryManager() {
        this.historyId = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int taskId = task.getId();
        if (historyId.containsKey(taskId)) {
            removeNode(historyId.get(taskId));
        }
        historyId.put(taskId, linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (historyId.containsKey(id)) {
            removeNode(historyId.get(id));
            historyId.remove(id);
        }
    }

    @Override
    public void removeAll(Set<Integer> tasksId) {
        tasksId.stream()
                .filter(id -> historyId.containsKey(id))
                .forEach(id -> {
                    removeNode(historyId.get(id));
                    historyId.remove(id);
                });
    }

    private void removeNode(Node<Task> node) {
        Node<Task> next = node.next;
        Node<Task> prev = node.prev;
        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.item = null;
    }

    private Node<Task> linkLast(Task task) {
        if (task == null) {
            return null;
        }
        Node<Task> last = this.tail;
        Node<Task> newNode = new Node<>(last, task, null);
        this.tail = newNode;
        if (last == null) {
            this.tail = newNode;
        } else {
            last.next = newNode;
        }
        if (this.head == null) {
            this.head = newNode;
        }
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>(historyId.size());

        for (Node<Task> x = head; x != null; x = x.next) {
            result.add(x.item);
        }
        return result;
    }

    public static class Node<T extends Task> {

        private T item;
        private Node<T> next;
        private Node<T> prev;

        public Node(Node<T> prev, T element, Node<T>  next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}