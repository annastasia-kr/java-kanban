package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subTasksId;

    public Epic(String name, String description) {
        super(name, description, null);
        subTasksId = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        this.subTasksId = epic.subTasksId;
    }

    public List<Integer> getSubTasksId() {
        return new ArrayList<>(subTasksId);
    }

    public boolean addToSubTasksId(Integer subTaskId){
        return subTasksId.add(subTaskId);
    }

    public boolean removeFromSubTasksId(Integer subTaskId){
        return subTasksId.remove(subTaskId);
    }

    @Override
    public String toString() {
        return "{name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ",subTasksId=" + subTasksId + '}';
    }
}
