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
    public Epic copy() {
        Epic copy = new Epic(this.getTaskName(), this.getDetails());
        copy.setID(this.getID());
        copy.listSaveSubTasksNum.addAll(this.listSaveSubTasksNum);
        copy.setStatus(this.getStatus());
        return copy;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "ID=" + getID() +
                ", taskName='" + getTaskName() + '\'' +
                ", details='" + getDetails() + '\'' +
                ", status=" + getStatus() +
                ", listSaveSubTasksNum=" + listSaveSubTasksNum +
                '}';
    }
}