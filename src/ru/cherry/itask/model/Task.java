package ru.cherry.itask.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private final String taskName;

    private String details;
    private Status status;
    protected TaskTypes taskType;

    private Duration durationTask;
    private LocalDateTime startTime;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String taskName, String details, Status status, LocalDateTime startTime, Duration durationTask) {
        this.taskName = taskName;
        this.details = details;
        this.status = status;
        this.taskType = TaskTypes.TASK;
        this.startTime = startTime;
        this.durationTask = durationTask;
    }

    public Task(String taskName, String details, Status status) {
        this.taskName = taskName;
        this.details = details;
        this.status = status;
        this.taskType = TaskTypes.TASK;
    }

    public String toCsv() {
        if (startTime == null || durationTask == null) {
            String dataTime = " ";
            String duration = " ";
            String endTime = " ";
            return String.format("%d,%s,%s,%s,%s,%s,%s,%s", id, taskType, taskName, status, details,
                    dataTime, duration, endTime);
        } else {
            return String.format("%d,%s,%s,%s,%s,%s,%s,%s", id, taskType, taskName, status, details,
                    startTime.format(formatter), durationTask.toMinutes(), getEndTime().format(formatter));
        }
    }

    public LocalDateTime getEndTime() {
        if (durationTask == null || startTime == null) {
            return null;
        }
        return startTime.plusMinutes(durationTask.toMinutes());
    }

    public static Task fromCsv(String value) {
        boolean isNotFullTask = false;
        String[] tasksElements = value.split(",");
        TaskTypes type = TaskTypes.valueOf(tasksElements[1]);
        String name = tasksElements[2];
        Status status = Status.valueOf(tasksElements[3]);
        String details = tasksElements[4];

        if (Objects.equals(tasksElements[5], " ")) {
            isNotFullTask = true;
        }

        switch (type) {
            case TASK:
                if (isNotFullTask) {
                    return new Task(name, details, status);
                } else {
                    LocalDateTime startTime = LocalDateTime.parse(tasksElements[5], formatter);
                    Duration durationTask = Duration.ofMinutes(Integer.parseInt(tasksElements[6]));
                    return new Task(name, details, status, startTime, durationTask);
                }
            case EPIC:
                return new Epic(name, details);
            case SUBTASK:
                int epicId = Integer.parseInt(tasksElements[8]);
                LocalDateTime startTime = LocalDateTime.parse(tasksElements[5], formatter);
                Duration durationTask = Duration.ofMinutes(Integer.parseInt(tasksElements[6]));
                return new SubTask(name, details, status, epicId, startTime, durationTask);
            default:
                return null;
        }
    }

    public Task copy() {
        Task copy = new Task(this.taskName, this.details, this.status, this.startTime, this.durationTask);
        copy.setID(this.id);
        return copy;
    }

    public boolean isOverlap(Task otherTask) {
        LocalDateTime thisEnd = this.getEndTime();
        LocalDateTime otherEnd = otherTask.getEndTime();
        if (this.startTime == null || thisEnd == null
                || otherTask.getStartTime() == null || otherEnd == null) {
            return false;
        }
        return this.startTime.isBefore(otherTask.getEndTime()) && otherTask.getStartTime().isBefore(getEndTime());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(details, task.details) &&
                status == task.status && taskType == task.taskType && Objects.equals(durationTask, task.durationTask)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, details, status, taskType, durationTask, startTime);
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

    public Duration getDurationTask() {
        return durationTask;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDurationTask(Duration durationTask) {
        this.durationTask = durationTask;
    }

    @Override
    public String toString() {
        if (startTime == null || durationTask == null) {
            return "Task{" +
                    "idOfTask=" + id +
                    ", taskName='" + taskName + '\'' +
                    ", details='" + details + '\'' +
                    ", status=" + status + '\'' +
                    '}';
        }
        return "Task{" +
                "idOfTask=" + id +
                ", taskName='" + taskName + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status + '\'' +
                ", startTime='" + startTime.format(formatter) + '\'' +
                ", duration='" + durationTask.toMinutes() + '\'' +
                ", endTime='" + getEndTime().format(formatter) + '\'' +
                '}';
    }
}