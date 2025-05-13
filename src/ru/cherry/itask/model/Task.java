package ru.cherry.itask.model;

import java.util.Objects;

public class Task {
    private int id;
    private final String taskName;
    private String details;
    private Status status;
    protected TaskTypes taskType;

    public Task(String taskName, String details, Status status) {
        this.taskName = taskName;
        this.details = details;
        this.status = status;
        this.taskType = TaskTypes.TASK;
    }

    public String toCsv() {
        return String.format("%d,%s,%s,%s,%s", id, taskType, taskName, status, details);
    }

    public static Task fromCsv(String value) {
        String[] tasksElements = value.split(",");
        TaskTypes type = TaskTypes.valueOf(tasksElements[1]);
        String name = tasksElements[2];
        Status status = Status.valueOf(tasksElements[3]);
        String details = tasksElements[4];

        switch (type) {
            case TASK:
                return new Task(name, details, status);
            case EPIC:
                return new Epic(name, details);
            case SUBTASK:
                int epicId = Integer.parseInt(tasksElements[5]);
                return new SubTask(name, details, status, epicId);
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(details, task.details) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, details, status);
    }

    public Task copy() {
        Task copy = new Task(this.taskName, this.details, this.status);
        copy.setID(this.id);
        return copy;
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }

    public enum TaskTypes {
        TASK,
        EPIC,
        SUBTASK
    }

    public TaskTypes getTaskTypes() {
        return taskType;
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
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "idOfTask=" + id +
                ", taskName='" + taskName + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                '}';
    }
}