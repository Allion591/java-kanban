package ru.cherry.itask.model;

import java.util.Objects;

public class Task {
    private int idOfTask;
    private final String taskName;
    private String details;
    private Status status;

    public Task(String taskName, String details, Status status) {
        this.taskName = taskName;
        this.details = details;
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return idOfTask == task.idOfTask && Objects.equals(taskName, task.taskName) && Objects.equals(details, task.details) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfTask, taskName, details, status);
    }

    public Task copy() {
        Task copy = new Task(this.taskName, this.details, this.status);
        copy.setID(this.idOfTask);
        return copy;
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public Status getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getID() {
        return idOfTask;
    }

    public void setID(int idOfTask) {
        this.idOfTask = idOfTask;
    }

    @Override
    public String toString() {
        return "Task{" +
                "idOfTask=" + idOfTask +
                ", taskName='" + taskName + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                '}';
    }
}