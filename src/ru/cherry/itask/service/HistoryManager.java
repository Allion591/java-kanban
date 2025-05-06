package ru.cherry.itask.service;

import ru.cherry.itask.model.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}