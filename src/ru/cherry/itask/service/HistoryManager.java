package ru.cherry.itask.service;

import ru.cherry.itask.model.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}