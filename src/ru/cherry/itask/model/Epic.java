package ru.cherry.itask.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> listSaveSubTasksNum;

    public Epic(String taskName, String details) {
        super(taskName, details, Status.NEW);
        listSaveSubTasksNum = new ArrayList<>();
    }

    public List<Integer> getSubtasksIDs() {
        return listSaveSubTasksNum;
    }

    public void addSubtaskId(int idSubTask) {
        if (idSubTask != this.getID()) {
            listSaveSubTasksNum.add(idSubTask);
        }
    }
    @Override
    public Epic copy() {
        Epic copy = new Epic(this.getTaskName(), this.getDetails());
        copy.setID(this.getID());
        copy.getSubtasksIDs().addAll(this.listSaveSubTasksNum);
        copy.setStatus(this.getStatus());
        return copy;
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + getID() +
                ", taskName='" + getTaskName() + '\'' +
                ", details='" + getDetails() + '\'' +
                ", status=" + getStatus() +
                ", listSaveSubTasksNum=" + listSaveSubTasksNum +
                '}';
    }
}