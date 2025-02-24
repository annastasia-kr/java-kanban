package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private LocalDateTime endTime;
    private List<Integer> subTasksId;

    public Epic(String name, String description) {
        super(name, description, null, null, null);
        subTasksId = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        this.subTasksId = epic.subTasksId;
        this.endTime = epic.endTime;
    }

    public List<Integer> getSubTasksId() {
        return new ArrayList<>(subTasksId);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean addToSubTasksId(Integer subTaskId) {
        return subTasksId.add(subTaskId);
    }

    public boolean removeFromSubTasksId(Integer subTaskId) {
        return subTasksId.remove(subTaskId);
    }

    @Override
    public String toString() {
        return "{name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subTasksId=" + subTasksId +
                ", startTime=" + getStartTime() +
                ", endTime=" + endTime +
                ", duration=" + getDuration() + '}';
    }
}
