package ru.cherry.itask.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> listSaveSubTasksNum;

    private LocalDateTime endTime;


    public Epic(String taskName, String details) {
        super(taskName, details, Status.NEW);
        listSaveSubTasksNum = new ArrayList<>();
        this.taskType = TaskTypes.EPIC;
    }

    public List<Integer> getSubtasksIDs() {
        return new ArrayList<>(listSaveSubTasksNum);
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
        copy.listSaveSubTasksNum.addAll(this.listSaveSubTasksNum);
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