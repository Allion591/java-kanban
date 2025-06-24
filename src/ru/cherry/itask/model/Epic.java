package ru.cherry.itask.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> listSaveSubTasksNum = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String taskName, String details) {
        super(taskName, details, Status.NEW);
        this.taskType = TaskTypes.EPIC;
    }

    public Epic() {
        super("", "", Status.NEW);
        this.taskType = TaskTypes.EPIC;
    }

    public List<Integer> getSubtasksIDs() {
        if (listSaveSubTasksNum != null) {
            return new ArrayList<>(listSaveSubTasksNum);
        } else {
            return new ArrayList<>();
        }
    }

    public void addSubtaskId(int idSubTask) {
        if (idSubTask != this.getID()) {
            listSaveSubTasksNum.add(idSubTask);
        }
    }

    public void removeSubtaskId(int id) {
        listSaveSubTasksNum.remove(Integer.valueOf(id));
    }

    @Override
    public String toCsv() {
        return super.toCsv();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Epic copy() {
        Epic copy = new Epic(this.getTaskName(), this.getDetails());
        copy.setID(this.getID());
        copy.setStartTime(this.getStartTime());
        copy.setDurationTask(this.getDurationTask());
        copy.setEndTime(this.getEndTime());
        if (this.listSaveSubTasksNum != null) {
            copy.listSaveSubTasksNum.addAll(this.listSaveSubTasksNum);
        } else {
            return copy;
        }
        copy.setStatus(this.getStatus());
        return copy;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        if (getStartTime() == null) {
            return "Epic{" +
                    "ID=" + getID() +
                    ", taskName='" + getTaskName() + '\'' +
                    ", details='" + getDetails() + '\'' +
                    ", status=" + getStatus() +
                    ", listSaveSubTasksNum=" + listSaveSubTasksNum +
                    '}';
        } else {
            return "Epic{" +
                    "ID=" + getID() +
                    ", taskName='" + getTaskName() + '\'' +
                    ", details='" + getDetails() + '\'' +
                    ", status=" + getStatus() +
                    ", listSaveSubTasksNum=" + listSaveSubTasksNum +
                    ", startTime='" + getStartTime().format(formatter) + '\'' +
                    ", duration='" + getDurationTask().toMinutes() + '\'' +
                    ",  endTime='" + getEndTime().format(formatter) + '\'' +
                    '}';
        }
    }
}