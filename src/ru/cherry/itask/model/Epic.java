package ru.cherry.itask.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> listSaveSubTasksNum;

    public Epic(String taskName, String details) {
        super(taskName, details, Status.NEW);
        listSaveSubTasksNum = new ArrayList<>();
    }

    public List<Integer> getSubtasksIDs() {
        return listSaveSubTasksNum;
    }

    public void addSubtaskId(int idSubTask) {
        listSaveSubTasksNum.add(idSubTask);
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