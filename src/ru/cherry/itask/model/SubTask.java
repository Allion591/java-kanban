package ru.cherry.itask.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String taskName, String details, Status status, int epicId,
                   LocalDateTime startTime, Duration durationTask) {
        super(taskName, details, status, startTime, durationTask);
        this.taskType = TaskTypes.SUBTASK;
        this.epicId = epicId;

    }

    @Override
    public String toCsv() {
        return String.format("%s,%d",
                super.toCsv(),
                epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public SubTask copy() {
        SubTask copy = new SubTask(this.getTaskName(), this.getDetails(), this.getStatus(),
                this.epicId, this.getStartTime(), this.getDurationTask());
        copy.setID(this.getID());
        return copy;
    }

    @Override
    public String toString() {
        return "subTask{" +
                "ID=" + getID() +
                ", taskName='" + getTaskName() + '\'' +
                ", details='" + getDetails() + '\'' +
                ", status=" + getStatus() +
                ", EpicId=" + epicId +
                ", startTime='" + getStartTime().format(formatter) + '\'' +
                ", duration='" + getDurationTask().toMinutes() + '\'' +
                ", endTime='" + getEndTime().format(formatter) + '\'' +
                '}';
    }
}