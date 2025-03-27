package service;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String taskName, String details, Status status, int epicId) {
        super(taskName, details, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + getID() +
                ", taskName='" + getTaskName() + '\'' +
                ", details='" + getDetails() + '\'' +
                ", status=" + getStatus() +
                ", EpicId=" + epicId +
                '}';
    }
}