package model;

import enumirations.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private Integer epicId;

    public SubTask(String name, String description, Status status, Duration duration, LocalDateTime startTime,
                   Integer epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        this.epicId = subTask.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "{name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", epicId=" + epicId + '}';
    }
}
