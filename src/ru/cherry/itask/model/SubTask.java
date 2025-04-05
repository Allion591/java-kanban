package ru.cherry.itask.model;

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
    public SubTask copy() {
        SubTask copy = new SubTask(this.getTaskName(), this.getDetails(), this.getStatus(), this.epicId);
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
                '}';
    }
}