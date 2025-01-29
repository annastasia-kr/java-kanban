package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private Map<Integer, Node> historyId;

    public InMemoryHistoryManager() {
        this.historyId = new HashMap<>();
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
    public void removeAll(Map<Integer, ? extends Task> map) {
        for (Integer key : map.keySet()) {
            if (historyId.containsKey(key)) {
                removeNode(historyId.get(key));
                historyId.remove(key);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(Node node) {
        Node next = node.next;
        Node prev = node.prev;
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

    private Node linkLast(Task task) {
        if (task == null) {
            return null;
        }
        Node last = tail;
        Node newNode = new Node(last, task, null);
        tail = newNode;
        if (last != null) {
            last.next = newNode;
        }
        if (head == null) {
            head = newNode;
        }
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>(historyId.size());

        for (Node x = head; x != null; x = x.next) {
            result.add(x.item);
        }
        return result;
    }

    public static class Node {

        private Task item;
        private Node next;
        private Node prev;

        public Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}