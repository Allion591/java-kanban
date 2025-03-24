package Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Task {

    private int id;
    private String taskName;
    private String details;
    private Status status;
    private List<Integer> subTasksId;
    private int epicId;

    public Task(int id, String taskName, String details, Status status) {
        this.id = id;
        this.taskName = taskName;
        this.status = status;
        this.details = details;
        this.subTasksId = new LinkedList<>();

    }
    public List<Integer> getSubTasksId() {
        return subTasksId;
    }
    public void setSubTasksId(List<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }
    public String getTaskName() {

        return taskName;
    }

    public void setTaskName(String taskName) {

        this.taskName = taskName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(details, task.details) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, details, status);
    }
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                ", subTasksId=" + subTasksId +
                ", epicId=" + epicId +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }

}